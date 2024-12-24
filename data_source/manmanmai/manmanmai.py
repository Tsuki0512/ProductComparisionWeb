import hashlib
import re
import requests
import execjs
from urllib.parse import quote
import time
import datetime
import tkinter as tk
from tkinter import ttk
import pyecharts.options as opts
from pyecharts.charts import Line
from pyecharts.render import make_snapshot
from snapshot_selenium import snapshot
from PIL import Image, ImageTk



def get_content(word):
    """获取ticket值"""
    link = f'https://tool.manmanbuy.com/HistoryLowest.aspx?url={word}'
    headers_1 = {
        'Cookie': 'Hm_lvt_85f48cee3e51cd48eaba80781b243db3=1703053522; _gid=GA1.2.806354424.1703053522; 60014_mmbuser=U1cNVFIHBj1UVwdcUwENUgUAVlAOUwJSAgUHUANQAlQGBwZUBgcLAA%3d%3d; acw_tc=784e2c9117030535779577779e37084d526e4cc1c30239a3bde271b0cddf65; ASP.NET_SessionId=e210i5v0rdntglshjd1kqpdn; Hm_lvt_01a310dc95b71311522403c3237671ae=1703053579; Hm_lpvt_85f48cee3e51cd48eaba80781b243db3=1703053740; _ga=GA1.1.1944431715.1689754950; _ga_1Y4573NPRY=GS1.1.1703053521.5.1.1703053818.0.0.0; Hm_lpvt_01a310dc95b71311522403c3237671ae=1703053818',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    }
    html_data = requests.get(url=link, headers=headers_1).text
    BasicAuth = re.findall('id="ticket" value="(.*?)"', html_data)[0]
    """获取加密的BasicAuth值"""
    f_1 = open('demo.js', encoding='utf-8').read()
    js_code_1 = execjs.compile(f_1)
    num = js_code_1.call('getTicket', BasicAuth)

    """获取token值"""
    t = int(time.time() * 1000)
    html = quote(word).upper().replace('/', '%2F')
    string = f"C5C3F201A8E8FC634D37A766A0299218KEY{html}METHODGETHISTORYTRENDT{t}C5C3F201A8E8FC634D37A766A0299218"
    MD5 = hashlib.md5()
    MD5.update(string.encode('utf-8'))
    token = MD5.hexdigest().upper()
    print('token: ', token)
    data = {
        'method': 'getHistoryTrend',
        'key': word,
        't': t,
        'token': token,
    }
    url = 'https://tool.manmanbuy.com/api.ashx'
    headers = {
        'Authorization': f'BasicAuth {num}',
        'Cookie': 'Hm_lvt_85f48cee3e51cd48eaba80781b243db3=1703053522; _gid=GA1.2.806354424.1703053522; 60014_mmbuser=U1cNVFIHBj1UVwdcUwENUgUAVlAOUwJSAgUHUANQAlQGBwZUBgcLAA%3d%3d; ASP.NET_SessionId=e210i5v0rdntglshjd1kqpdn; Hm_lvt_01a310dc95b71311522403c3237671ae=1703053579; acw_tc=784e2cad17030688190068163e4f4bfbc2c4c6b6588f89462ab30aa8e802a3; Hm_lpvt_85f48cee3e51cd48eaba80781b243db3=1703069309; _gat_gtag_UA_145348783_1=1; _ga_1Y4573NPRY=GS1.1.1703068787.6.1.1703069309.0.0.0; _ga=GA1.1.1944431715.1689754950; Hm_lpvt_01a310dc95b71311522403c3237671ae=1703069315',
        'Host': 'tool.manmanbuy.com',
        'Origin': 'https://tool.manmanbuy.com',
        'Pragma': 'no-cache',
        'Referer': 'https://tool.manmanbuy.com/HistoryLowest.aspx?url=https%3A%2F%2Fitem.jd.com%2F10061379759087.html',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    }
    response = requests.post(url=url, data=data, headers=headers)
    json_data = response.json()
    title = json_data['data']['spName'] # 标题
    siteName = json_data['data']['siteName'] # 商城
    lowerPrice = json_data['data']['lowerPrice'] # 最低价
    lowerDate = json_data['data']['lowerDate'].split('T')[0] # 最低价时间
    currentPrice = json_data['data']['currentPrice'] # 当前价
    info_list = json_data['data']['datePrice'].split('],[') # 历史价
    dit = {
        'title': title,
        'siteName': siteName,
        'lowerPrice': lowerPrice,
        'lowerDate': lowerDate,
        'currentPrice': currentPrice,
    }
    print(dit)
    date_list = []
    price_list = []
    for info in info_list:
        index = info.split(',')
        num = index[0].replace('[', '')[:-3]
        date = str(datetime.datetime.fromtimestamp(int(num))).split(' ')[0]
        price = index[1]
        # prefer = ''.join(index[2:])
        date_list.append(date)
        price_list.append(price)

    return dit, date_list, price_list


def View(dit, date_list, price_list):
    print('可视化')
    c = (
        Line()
        .add_xaxis(date_list)
        .add_yaxis("", price_list)
        .set_global_opts(
            xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=-15)),
            title_opts=opts.TitleOpts(title=f"{dit['title']}", subtitle="价格走势"),
        )
        # .render("line_base.html")
    )
    # webbrowser.open('line_base.html')
    make_snapshot(snapshot, c.render(), "折线图.png")

    # 显示图片的代码
    img = Image.open('折线图.png')
    img = img.resize((962, 566), Image.LANCZOS)
    img = ImageTk.PhotoImage(img)
    panel = tk.Label(root, image=img)
    panel.image = img
    panel.pack(pady=20)


def Show():
    word = key_va.get()
    dit, date_list, price_list = get_content(word)
    tree_view.insert('', 1,values=(
            dit['title'], dit['siteName'], dit['lowerPrice'], dit['lowerDate'], dit['currentPrice']))
    View(dit, date_list, price_list)


root = tk.Tk()
root.title('商品历史价格走势')
root.geometry('989x906+200+200')

frame = tk.Frame(root)
frame.pack(anchor=tk.W, pady=5)
tk.Label(frame, text='查商品历史价格走势', font=('微软雅黑', 20)).pack()

input_frame = tk.LabelFrame(
    root,
    text='京东、天猫、淘宝、拼多多、亚马逊、苏宁、当当、考拉、严选、国美等商品网址',
    font=('微软雅黑', 15)
)
input_frame.pack(fill=tk.BOTH, pady=10)
key_va = tk.StringVar()
tk.Entry(input_frame, width=100, relief='flat', textvariable=key_va).pack(anchor=tk.W, padx=20, pady=10)
tk.Button(input_frame, relief='flat', text='商品历史价格查询', font=('微软雅黑', 12), bg='#fbc21a',command=Show).pack(anchor=tk.W, padx=20, pady=10)


"""输入效果表格"""
show_frame = tk.Frame()
show_frame.pack(fill=tk.BOTH)
# 1. 创建字段
columns = ("title", "siteName", "lowerPrice", "lowerDate", 'currentPrice')
columns_value = ('商品', '商城', '最低价', '最低价日期', '当前价')
# 2. 创建表格对象
tree_view = ttk.Treeview(show_frame, show="headings", columns=columns, height=1)
# 3. 给表格添加字段名
tree_view.column('title', width=80, anchor='center')
tree_view.column('siteName', width=80, anchor='center')
tree_view.column('lowerPrice', width=80, anchor='center')
tree_view.column('lowerDate', width=80, anchor='center')
tree_view.column('currentPrice', width=80, anchor='center')

# 4. 设置字段在页面上显示的内容
tree_view.heading('title', text='商品')
tree_view.heading('siteName', text='商城')
tree_view.heading('lowerPrice', text='最低价')
tree_view.heading('lowerDate', text='最低价日期')
tree_view.heading('currentPrice', text='当前价')
# 5. 将表格对象布局到页面上
tree_view.pack(fill=tk.BOTH, expand=True)


root.mainloop()