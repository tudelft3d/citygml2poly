#!/usr/bin/env python
# encoding: utf-8


import sys
import os
import subprocess
import shutil
import glob

# INFILE = '/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml'
INFILE = '/Users/hugo/Dropbox/data/citygml/os_2buildings.xml'

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
  # print "Number of solids in file:", len(glob.glob('tmp/*.poly'))

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
  val3dity =   '/Users/hugo/Library/Developer/Xcode/DerivedData/val3dity-btcvseqwbnkwbueknlulczqmjyqt/Build/Products/Debug/val3dity'
  # val3dity = '/Users/hugo/project/val3dity/trunk/val3dity'
  i = 0
  print "Number of solids in file:", len(dFiles)
  xmlsolids = []
  for solidname in dFiles:
    str1 = val3dity + " -withids -xml " +  " ".join(dFiles[solidname])
    op = subprocess.Popen(str1.split(' '),
                          stdout=subprocess.PIPE, 
                          stderr=subprocess.PIPE)
    R = op.poll()
    if R:
       res = op.communicate()
       raise ValueError(res[1])
    o =  op.communicate()[0]
    o = '\t<Solid>\n\t\t<id>' + solidname + '</id>\n' + o + '\t</Solid>'
    xmlsolids.append(o)

  totalxml = []
  totalxml.append('<ValidatorContext>')
  totalxml.append('\t<inputFile>' + INFILE + '</inputFile>')
  totalxml.append("\n".join(xmlsolids))
  totalxml.append('</ValidatorContext>')
  
  print "\n", '-'*33, '\n '
  print "\n".join(totalxml)
  fout = open('../report.xml', 'w')
  fout.write('\n'.join(totalxml))
  fout.close()

    
# 4. wipe the tmp folder
  os.chdir('../')
  shutil.rmtree('tmp')


if __name__ == '__main__':
  main()

