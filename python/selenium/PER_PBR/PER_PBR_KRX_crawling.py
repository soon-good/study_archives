#-*- coding:utf-8 -*-

import os
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

import time
import datetime

download_dir = os.path.dirname(os.path.realpath('__file__'))


def enable_download_headless(browser,_download_dir):
    browser.command_executor._commands["send_command"] = ("POST", '/session/$sessionId/chromium/send_command')
    params = {'cmd':'Page.setDownloadBehavior', 'params': {'behavior': 'allow', 'downloadPath': _download_dir}}
    browser.execute("send_command", params)


chrome_options = Options()
# chrome_options.add_argument("--headless")
chrome_options.add_argument("--window-size=1920x1080")
chrome_options.add_argument("--disable-notifications")
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--verbose')
chrome_options.add_experimental_option("prefs", {
        "download.default_directory": download_dir,
        "download.prompt_for_download": False,
        "download.directory_upgrade": True,
        "safebrowsing_for_trusted_sources_enabled": False,
        "safebrowsing.enabled": False
})
chrome_options.add_argument('--disable-gpu')
chrome_options.add_argument('--disable-software-rasterizer')


krx_url = "http://marketdata.krx.co.kr/mdi#document=13020401"
ONE_SECOND = 1000;
SLEEP_SECOND = ONE_SECOND * 10;

URL_KRX_SEARCH = "http://marketdata.krx.co.kr/mdi#document=13020401";
ID_RADIO_INDIVIDUAL = "gubun1e4da3b7fbbce2345d7772b0674a318d5";
ID_INPUT_COMPANYNAME = "isu_cdnm1679091c5a880faf6fb5e6087eb1b2dc";

ID_POPUP_BTN_SEARCH_COMPANY_CODE = "finderbtn1679091c5a880faf6fb5e6087eb1b2dc";
# searchTextb6d767d2f8ed5d21a44b0e5886680cb9
ID_POPUP_INPUT_SEARCH_COMPANY_CODE = "searchText70efdf2ec9b086079795c442636b55fb";
ID_POPUP_BTN_OK = "btnid70efdf2ec9b086079795c442636b55fb";
# btnid70efdf2ec9b086079795c442636b55fb

ID_INPUT_DATE_START = "fromdatec9f0f895fb98ab9159f51fd0297e236d";
VALUE_INPUT_DATE_START = "20200101";

ID_INPUT_DATE_END = "todatec9f0f895fb98ab9159f51fd0297e236d";
VALUE_INPUT_DATE_END = "20201231";
ID_BTN_OK = "btnidc81e728d9d4c2f636f067f89cc14862c";
selector_for_test_loaded = "td[data-name='isu_cd']"

SEARCH_COMPANY_NAME = "씨에스윈드"
SEARCH_COMPANY_CODE_N_NAME = "A112610/씨에스윈드"

list_companys = [
    {"COMPANY_NM": "씨에스윈드", "COMPANY_CODE_NM": "A112610/씨에스윈드", "COMPANY_NAME_EN": "CSWIND"},
    {"COMPANY_NM": "동화기업", "COMPANY_CODE_NM": "A025900/동화기업", "COMPANY_NAME_EN": "DONGWHA"},
    {"COMPANY_NM": "삼강엠앤티", "COMPANY_CODE_NM": "A100090/삼강엠앤티", "COMPANY_NAME_EN": "SAMGANG_M_AND_T"},
    {"COMPANY_NM": "두산퓨얼셀", "COMPANY_CODE_NM": "A336260/두산퓨얼셀", "COMPANY_NAME_EN": "DUSAN_FUEL_CELL"},
    {"COMPANY_NM": "하이트진로", "COMPANY_CODE_NM": "A000080/하이트진로", "COMPANY_NAME_EN": "HITE_JINRO"},
    {"COMPANY_NM": "삼성전자", "COMPANY_CODE_NM": "A005930/삼성전자", "COMPANY_NAME_EN": "SAMSUNG_ELECTRIC"},
    {"COMPANY_NM": "일진다이아", "COMPANY_CODE_NM": "A081000/일진다이아", "COMPANY_NAME_EN": "ILJIN_DIA"},
]

for l in list_companys:
    print("company_name : {} , company_code_name : {}".format(l['COMPANY_NM'], l['COMPANY_CODE_NM']))


def wait_until_id_loaded(element_id):
    selector = '#' + element_id
    WebDriverWait(browser, 50).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, selector)), "asdfasdf"
    )


def wait_until_by_selector(selector):
    WebDriverWait(browser, 50).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, selector)), "asdfasdf"
    )


def get_element_by_id(element_id):
    return browser.find_element(By.CSS_SELECTOR, '#' + element_id)


def get_element_by_selector(selector):
    return browser.find_element(By.CSS_SELECTOR, selector)


# name = searchText
# class = "func-finder-searchInput"

try:
    browser = webdriver.Chrome(options=chrome_options)
    enable_download_headless(browser, download_dir)
    browser.get(krx_url)

    for l in list_companys:
        print("company_name : {} , company_code_name : {}".format(l['COMPANY_NM'], l['COMPANY_CODE_NM']))

        wait_until_by_selector(selector_for_test_loaded)

        get_element_by_id(ID_RADIO_INDIVIDUAL).click()
        wait_until_by_selector('#' + ID_INPUT_COMPANYNAME)
        print("load complete " + ID_INPUT_COMPANYNAME)

        get_element_by_id(ID_INPUT_COMPANYNAME).clear()
        get_element_by_id(ID_INPUT_COMPANYNAME).send_keys(l['COMPANY_CODE_NM'])
        #         wait_until_by_selector('#'+ID_POPUP_BTN_SEARCH_COMPANY_CODE)
        time.sleep(10)
        get_element_by_id(ID_POPUP_BTN_SEARCH_COMPANY_CODE).click()
        print("종목검색 팝업 로드")

        #         wait_until_by_selector('.CI-GRID-BODY-TABLE')
        print("버퍼링 아이콘 gif 사라질때까지 대기")
        time.sleep(10)  # 추후 wait until 로 교체

        print("종목 검색 인풋박스 대입")
        get_element_by_selector(".func-finder-searchInput").send_keys(l["COMPANY_NM"])
        #         get_element_by_id(ID_POPUP_INPUT_SEARCH_COMPANY_CODE).send_keys(l["COMPANY_NM"])

        print("팝업 > 검색 버튼 클릭")
        #         get_element_by_id(ID_POPUP_BTN_OK).click()
        get_element_by_selector('.btn-board-search').click()
        #     get_element_by_id(ID_POPUP_BTN_SEARCH_COMPANY_CODE).click()

        print("종목결과 모두 나올때까지 대기")
        time.sleep(5)  # 추후 wait until 로 교체

        script_for_search = """
            document.querySelector('.CI-GRID-BODY-TABLE-TBODY')
                    .children[0].children[1].children[0]
                    .click();
        """

        browser.execute_script(script_for_search)

        script_change_start_date = """
            document.querySelector('#{}').value = {}
        """.format(ID_INPUT_DATE_START, VALUE_INPUT_DATE_START)

        script_change_end_date = """
            document.querySelector('#{}').value = {}
        """.format(ID_INPUT_DATE_END, VALUE_INPUT_DATE_END)

        print(script_change_start_date)
        print(script_change_end_date)

        browser.execute_script(script_change_start_date)
        browser.execute_script(script_change_end_date)

        get_element_by_id(ID_BTN_OK).click()

        chart_btn_index = 0
        excel_btn_index = 4
        csv_btn_index = 6
        class_btn_group = '.button-mdi-group'

        script_click_download = """
            document.querySelector('{}')
                .children[{}]
                .click();
        """.format(class_btn_group, csv_btn_index)

        print("script :: {}".format(script_click_download))
        browser.execute_script(script_click_download)

        date_now = datetime.datetime.now()
        str_date = date_now.strftime("%Y_%m_{}".format(l["COMPANY_NAME_EN"]))
        print(str_date)

        src_file = download_dir + "/" + "data.csv"
        target_file = download_dir + "/" + l["COMPANY_NAME_EN"] + ".csv"

        time.sleep(10)
        os.rename(src_file, target_file)

finally:
    browser.quit()

