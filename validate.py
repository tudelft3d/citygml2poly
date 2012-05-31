#!/usr/bin/env python
# encoding: utf-8


import sys
import os
import subprocess
import shutil
import glob

INFILE = '/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml'

def main():

# 1. create and/or clear the tmp folder
  if not os.path.exists("tmp"):
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
  dFiles = {}
  for f in os.listdir('.'):
    if f[-4:] == 'poly':
      i = (f.split('.poly')[0]).rfind('.')
      f1 = f[:i]
      if f1 not in dFiles:
        dFiles[f1] = [f]
      else:
        dFiles[f1].append(f)
  val3dity = '/Users/hugo/Library/Developer/Xcode/DerivedData/val3dity-btcvseqwbnkwbueknlulczqmjyqt/Build/Products/Debug/val3dity'
  i = 0
  for solidname in dFiles:
    str1 = val3dity + " -withids -xml " +  " ".join(dFiles[solidname])
    print str1
    os.system(str1)
    i += 1
    if i == 3:
      break

# 4. wipe the tmp folder
  os.chdir('../')
  shutil.rmtree('tmp')


if __name__ == '__main__':
  main()

