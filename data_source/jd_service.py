import re
from bs4 import BeautifulSoup
import requests
from urllib.parse import quote
import json

JD_SEARCH_URL = 'https://search.jd.com/Search'
JD_COMMON_HEADERS = {
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
    'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
    'Sec-Ch-Ua': '"Google Chrome";v="125", "Chromium";v="125", "Not.A/Brand";v="24"',
    'Sec-Ch-Ua-Mobile': '?0',
    'Sec-Ch-Ua-Platform': '"Windows"',
    'Sec-Fetch-Dest': 'document',
    'Sec-Fetch-Mode': 'navigate',
    'Sec-Fetch-Site': 'none',
    'Sec-Fetch-User': '?1',
    'Upgrade-Insecure-Requests': '1',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36',
}

def jd_request_search(keyword: str, cookie: str, offset: int = 0, limit: int = 30) -> dict:
    """
    请求京东获取搜索信息
    """
    try:
        print(f"\n=== 开始京东搜索 ===")
        print(f"关键词: {keyword}")
        print(f"请求数量: {limit}")
        
        results = []
        page_size = 30
        start_page = int(offset / page_size) + 1
        end_page = int((offset + limit - 1) / page_size) + 1
        total = 0

        # 处理 cookie
        # 清理 cookie 中的空白字符和换行符
        cookie = cookie.strip().replace('\n', '').replace('\r', '')
        
        # 将 cookie 字符串转换为字典
        cookie_dict = {}
        for item in cookie.split(';'):
            if '=' in item:
                name, value = item.strip().split('=', 1)
                cookie_dict[name.strip()] = value.strip()

        # 同步循环获取每一页的数据
        for page in range(start_page, end_page + 1):
            data = jd_fetch_page(keyword, page, cookie)
            if data:
                results.extend(data.get('items', []))
                total = data.get('total', 0)

        # 截取需要的结果
        ret = {
            'code': 200,
            'msg': 'success',
            'data': results[(offset % page_size):(offset % page_size + limit)],
            'total': total
        }
        
        print(f"京东搜索结果数量: {len(ret['data'])}")
        
        # 直接返回 JSON 字符串，不打印
        return ret

    except Exception as e:
        print(f"搜索过程出错: {str(e)}")
        return {
            'code': 500,
            'msg': str(e),
            'data': [],
            'total': 0
        }

def jd_fetch_page(keyword: str, page: int, cookie: str) -> dict:
    """
    获取京东单页搜索结果
    """
    try:
        encoded_keyword = quote(keyword)
        url = f'{JD_SEARCH_URL}?keyword={encoded_keyword}&page={page}&s={(page-1)*30}'
        headers = {'cookie': cookie}
        headers.update(JD_COMMON_HEADERS)

        print(f'请求京东搜索URL: {url}')
        response = requests.get(url, headers=headers)
        
        if response.status_code != 200:
            print(f'京东搜索请求失败: {response.status_code}')
            return {}
        #print(response.text)
        return parse_jd_search_html(response.text)

    except Exception as e:
        print(f"京东搜索请求失败: {str(e)}")
        return {}

def parse_jd_search_html(html: str) -> dict:
    """
    解析京东搜索结果HTML
    """
    try:
        soup = BeautifulSoup(html, 'html.parser')
        items = []
        
        # 获取商品列表
        product_list = soup.find_all('li', class_='gl-item')
        print(f"找到商品数量: {len(product_list)}")
        
        for product in product_list:
            try:
                # 获取商品ID
                product_id = product.get('data-sku')
                if not product_id:
                    continue

                # 获取商品价格
                price_element = product.find('div', class_='p-price').find('i')
                if not price_element or '?' in price_element.text:
                    continue
                price = price_element.text.strip()

                # 获取商品标题
                title_element = product.find('div', class_='p-name').find('em')
                if not title_element:
                    continue
                title = title_element.get_text(strip=True)

                # 获取商品图片
                img_element = product.find('div', class_='p-img').find('img')
                if not img_element:
                    continue
                image_url = 'https:' + img_element.get('data-lazy-img', '')

                # 获取商品链接
                link_element = product.find('div', class_='p-name').find('a')
                if not link_element:
                    continue
                link = 'https:' + link_element.get('href', '')

                # 获取店铺信息
                shop_element = product.find('div', class_='p-shop')
                shop_name = shop_element.get_text(strip=True) if shop_element else '京东自营'

                item = {
                    'barcode': product_id,
                    'productname': title,
                    'platform': '京东',
                    'current_price': float(price),
                    'image_url': image_url,
                    'specification': shop_name,
                    'link': link,
                    'historical_prices': json.dumps({
                        'current': price,
                        'original': price
                    })
                }
                items.append(item)

            except Exception as e:
                print(f"解析商品数据失败: {str(e)}")
                continue

        # 获取总结果数
        total = 0
        total_element = soup.find('div', id='J_topPage')
        if total_element:
            total_text = total_element.find('i').text if total_element.find('i') else '0'
            try:
                total = int(total_text)
            except ValueError:
                total = len(items)

        return {
            'items': items,
            'total': total
        }

    except Exception as e:
        print(f"解析京东搜索结果失败: {str(e)}")
        return {'items': [], 'total': 0}

if __name__ == "__main__":
    import sys
    
    # 获取命令行参数
    keyword = sys.argv[1]
    cookie = sys.argv[2]
    offset = int(sys.argv[3])
    limit = int(sys.argv[4])
    
    # 执行搜索并输出结果
    result = jd_request_search(keyword, cookie, offset, limit)
    # 确保只输出一次 JSON 结果
    print(json.dumps(result, ensure_ascii=False))