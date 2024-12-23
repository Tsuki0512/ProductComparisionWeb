import re
from bs4 import BeautifulSoup
import requests
from urllib.parse import quote
import json
import time
import hashlib

TB_HOST = 'https://h5api.m.taobao.com'
TB_COMMON_HEADERS = {
    'Accept': '*/*',
    'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
    'Referer': 'https://s.taobao.com/search',
    'Sec-Ch-Ua': '"Google Chrome";v="125", "Chromium";v="125", "Not.A/Brand";v="24"',
    'Sec-Ch-Ua-Mobile': '?0',
    'Sec-Ch-Ua-Platform': '"Windows"',
    'Sec-Fetch-Dest': 'script',
    'Sec-Fetch-Mode': 'no-cors',
    'Sec-Fetch-Site': 'same-site',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36',
}

APPKEY = '12574478'

def sign(token, tme, appKey, data):
    st = f"{token}&{tme}&{appKey}&{data}"
    return hashlib.md5(st.encode('utf-8')).hexdigest()

def get_token(cookie):
    get_cookies = dict([l.split("=", 1) for l in cookie.split("; ")])
    _m_h5_tk = get_cookies.get('_m_h5_tk', '')
    token = _m_h5_tk.split('_')[0]
    return token

def tb_request_search(keyword: str, cookie: str, offset: int = 0, limit: int = 10) -> dict:
    try:
        print(f"\n=== 开始淘宝搜索 ===")
        print(f"关键词: {keyword}")
        print(f"请求数量: {limit}")
        
        results = []
        page_size = 48
        start_page = int(offset / page_size) + 1

        # 获取数据
        data = tb_fetch_page(keyword, cookie, start_page)
        items = data.get('itemsArray', [])
        
        print(f"\n=== 搜索统计 ===")
        print(f"获取到原始商品数量: {len(items)}")

        # 处理每个商品，但限制数量
        processed_count = 0
        for index, item in enumerate(items):
            if len(results) >= limit:
                break
                
            try:
                # 处理商品数据
                processed_item = process_item(item)
                if processed_item:
                    results.append(processed_item)
                    processed_count += 1

            except Exception as e:
                print(f"处理商品 {index + 1} 时出错: {str(e)}")
                continue

        print(f"\n=== 处理结果统计 ===")
        print(f"成功处理商品数: {processed_count}")
        print(f"实际返回商品数: {len(results)}")

        return {'results': results}

    except Exception as e:
        print(f"搜索过程出错: {str(e)}")
        return {'results': []}

def process_item(item):
    # ID处理
    item_id = None
    for field in ['uniqpid', 'itemId', 'item_id', 'id', 'nid']:
        item_id = item.get(field)
        if item_id:
            break

    if not item_id and 'utLogMap' in item:
        for field in ['uniqpid', 'itemId', 'item_id', 'id', 'nid']:
            item_id = item['utLogMap'].get(field)
            if item_id:
                break

    if not item_id:
        return None

    # 基本信息处理
    item_id = str(item_id)
    title = BeautifulSoup(item.get('title', ''), 'html.parser').get_text()
    price = (item.get('utLogMap', {}).get('price') or 
            item.get('price') or 
            item.get('priceShow', {}).get('price') or 
            item.get('priceWithRate') or 
            '0.00')
    
    # 图片处理
    pic_path = None
    for field in ['pic', 'picUrl', 'img', 'imgUrl', 'picPath', 'pic_path']:
        pic_path = item.get(field)
        if pic_path:
            break

    if not pic_path and 'utLogMap' in item:
        for field in ['pic', 'picUrl', 'img', 'imgUrl', 'picPath', 'pic_path']:
            pic_path = item['utLogMap'].get(field)
            if pic_path:
                break

    if not pic_path:
        pic_path = 'https://img.alicdn.com/imgextra/i4/O1CN01Z5paLz1O0zuCC7osS_!!6000000001644-55-tps-83-82.svg'
    else:
        pic_path = pic_path.replace('60x60', '360x360')

    return {
        'id': item_id,
        'name': title,
        'price': price,
        'imageURL': pic_path,
        'platform': '淘宝',
        'shopName': item.get('nick') or item.get('shopName') or item.get('sellerNick') or '未知店铺',
        'link': f"https://item.taobao.com/item.htm?id={item_id}"
    }

def tb_fetch_page(keyword: str, cookie: str, page: int) -> dict:
    try:
        cookie = cookie.strip()
        headers = {'cookie': cookie}
        headers.update(TB_COMMON_HEADERS)
        search_query = tb_pack_search_query(cookie, keyword, page)
        url = f'{TB_HOST}/h5/mtop.relationrecommend.wirelessrecommend.recommend/2.0/{search_query}'

        print(f'\n=== 发送淘宝请求 ===')
        print(f'URL: {url}')
        
        resp = requests.get(url, headers=headers)
        print(f'响应状态码: {resp.status_code}')

        res_str = re.sub(r'^\s*callback\(|\);?\s*$', '', resp.text)
        res = json.loads(res_str)
        
        if 'url' in res.get('data', {}) and 'punish' in res['data']['url']:
            print("\n=== 检测到验证码请求 ===")
            return {'itemsArray': []}
        
        return {
            'itemsArray': res.get('data', {}).get('itemsArray', [])
        }

    except Exception as e:
        print(f"\n=== 请求失败 ===")
        print(f"错误信息: {str(e)}")
        return {'itemsArray': []}

def tb_pack_search_query(cookie, keyword, page):
    quote_keyword = quote(keyword, 'utf-8')
    str_data = f'{{"appId":"34385","params":"{{\\"page\\":{page},\\"q\\":\\"{quote_keyword}\\",\\"n\\":48}}"}}'
    quote_data = quote(str_data, 'utf-8')
    timestamp = str(time.time()).replace('.', '')[0:13]
    token = get_token(cookie)
    sgn = sign(token, timestamp, APPKEY, str_data)
    return f'?jsv=2.7.2&appKey={APPKEY}&t={timestamp}&sign={sgn}&api=mtop.relationrecommend.wirelessrecommend.recommend&v=2.0&type=jsonp&data={quote_data}'

if __name__ == "__main__":
    import sys
    
    # 获取命令行参数
    keyword = sys.argv[1]
    cookie = sys.argv[2]
    offset = int(sys.argv[3])
    limit = int(sys.argv[4])
    
    # 执行搜索
    result = tb_request_search(keyword, cookie, offset, limit)
    
    # 输出 JSON 结果
    print(json.dumps(result))
