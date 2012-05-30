#!/usr/bin/env python
# encoding: utf-8
"""
untitled.py

Created by Hugo Ledoux on 2012-05-30.
Copyright (c) 2012 __MyCompanyName__. All rights reserved.
"""

import sys
import os
import subprocess
import shutil
import glob

INFILE = '/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml'

def main():

# 1. create and/or clear the tmp folder
  if not os.path.exists("tmp"):
    print "create new one"
    os.mkdir("tmp")
  else:
    shutil.rmtree("tmp")
    os.mkdir("tmp")

# 2. create and/or clear the tmp folder
  print "Processing file:", INFILE
  print "Parsing the file..."
  cmd = "./run.sh " + INFILE + " tmp"
  subprocess.call(cmd, shell=True)
  print "Done"
  print "Number of buildings in file:", len(glob.glob('tmp/*.poly'))

# 3. validate each building/shell
  os.chdir('tmp')
  # print len(glob.glob('*.poly'))
  lsFiles =[]
  for f in os.listdir('.'):
    lsFiles.append(f)
  print len(lsFiles)
  val3dity = '/Users/hugo/Library/Developer/Xcode/DerivedData/val3dity-btcvseqwbnkwbueknlulczqmjyqt/Build/Products/Debug/val3dity'

  i = 0
  for f in lsFiles:
    strr = val3dity + " " + f
    print strr
    os.system(val3dity + " -withids -xml " + f)
    i += 1
    if i == 3:
      sys.exit()


if __name__ == '__main__':
  main()

