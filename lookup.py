#!/usr/bin/env python

import requests
user = "HackZurich"
password = 'mKw%VY<7.Yb8D!G-'
url = 'https://backend.scango.ch/api/v01/items/'

print user
print password

res = requests.get(url, auth=(user, password))

status = res.status_code
text   = res.text
