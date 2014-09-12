#!/usr/bin/python3
from subprocess import call
import os
import sys

workspace=os.environ.get('WORKSPACE')
original_job_name=os.environ.get('original_job_name')
original_build_number=os.environ.get('original_build_number')

label_data="[build_label] "

labels = {}

job_name=os.environ.get('JOB_NAME')
build_number=os.environ.get('BUILD_NUMBER')
manufacturing_version=os.environ.get('WEBOS_DISTRO_MANUFACTURING_VERSION')
webos_distro_build_id=os.environ.get('WEBOS_DISTRO_BUILD_ID')
mass_production=os.environ.get('MP')

webos_distro_build_id=webos_distro_build_id.replace("\"", "")

print("[info] MP : %s" % ( mass_production ))
if not mass_production == "YES":
    print("[build_label] <li>WEBOS_DISTRO_BUILD_ID : %s</li><li>%s</li><li>Use %s #%s</li>" % (webos_distro_build_id, manufacturing_version, original_job_name, webos_distro_build_id))
    sys.exit(0)

#Set Download URLs
downloadHQ= "http://webos-ci.lge.com/download/starfish/%s_hq/%s/" % ( job_name, original_build_number)
label_data="%s <li><a href=\"%s\"> Download in HQ : #%s </a></li>" % (label_data, downloadHQ, original_build_number) 
downloadHQ= "http://ushquc001.palm.com/official_26/%s_hq/%s/" % ( job_name, original_build_number)
label_data="%s <li><a href=\"%s\"> Download in SVL : #%s </a></li>" % (label_data, downloadHQ, original_build_number) 


#check production build
production_return_code = os.system("grep '^IMAGE_FEATURES =' %s/BUILD-ARTIFACTS/*/*/image-info.txt|grep -w -q webos-production-image" % (workspace) )

if production_return_code == 0:
    labels['MP (Mass Production)'] = ['#00FF00', '']

#Insert Version Information
build_id_file="%s/BUILD-ARTIFACTS/build-id.txt" % (workspace)
manufacturing_version=""
distro_version=""
f = open(build_id_file,"r")
for line in f:
    line_data = line.rstrip('\n')
    if "WEBOS_DISTRO_MANUFACTURING_VERSION" in line_data:
        line_data = line_data.replace("WEBOS_DISTRO_MANUFACTURING_VERSION = ", "")
        manufacturing_version = line_data.replace("\"", "")
    if "DISTRO_VERSION" in line_data:
        line_data = line_data.replace("DISTRO_VERSION", "")
        line_data = line_data.replace(" ", "")
        line_data = line_data.replace("=", "")
        line_data = line_data.replace("\"", "")
        distro_version = line_data.replace("DISTRO_VERSION", "")
labels[manufacturing_version + " / " + distro_version ] = ['cyan', '']
f.close()

if len(labels) != 0:
    for k, v in labels.items():
        label_data="%s <li><font style=\"background-color:%s;width:100px\" > %s </font></li>" % (label_data, v[0], k)

print(label_data)
