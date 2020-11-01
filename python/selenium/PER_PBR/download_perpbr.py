from selenium import webdriver
from selenium.webdriver.common.keys import Keys

from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

krx_url = 'http://marketdata.krx.co.kr/mdi#document=13020401'
ffdriver = '/Users/kyle.sgjung/env/webdriver/geckodriver'

ffdriver = webdriver.Firefox()

ffdriver.get(krx_url)
span = ffdriver.find_elements_by_css_selector('span.button-mdi-group button')[2]
span.click()
ffdriver.quit()
