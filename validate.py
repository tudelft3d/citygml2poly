#!/usr/bin/env python
# encoding: utf-8

import Tkinter, Tkconstants, tkFileDialog
import sys
import os
import subprocess
import shutil
import glob
from lxml import etree
from StringIO import StringIO

# INFILE = '/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml'
# INFILE = '/Users/hugo/Dropbox/data/citygml/os_2buildings.xml'
# INFILE = '/Users/hugo/Dropbox/data/citygml/DenHaag11Building1.xml'

dErrors = {
          100: 'DUPLICATE_POINTS',
          110: 'RING_NOT_CLOSED',
          200: 'INNER_RING_WRONG_ORIENTATION',
          210: 'NON_PLANAR_SURFACE',
          220: 'SURFACE_PROJECTION_INVALID',
          221: 'INNER_RING_INTERSECTS_OUTER',
          222: 'INNER_RING_OUTSIDE_OUTER',
          223: 'INNER_OUTER_RINGS_INTERSECT',
          224: 'INTERIOR_OF_RING_NOT_CONNECTED',
          300: 'NOT_VALID_2_MANIFOLD',
          301: 'SURFACE_NOT_CLOSED',
          302: 'DANGLING_FACES',
          303: 'FACE_ORIENTATION_INCORRECT_EDGE_USAGE',
          304: 'FREE_FACES',
          305: 'SURFACE_SELF_INTERSECTS',
          306: 'VERTICES_NOT_USED',
          310: 'SURFACE_NORMALS_BAD_ORIENTATION',
          400: 'SHELLS_FACE_ADJACENT',
          410: 'SHELL_INTERIOR_INTERSECT',
          420: 'INNER_SHELL_OUTSIDE_OUTER',
          430: 'INTERIOR_OF_SHELL_NOT_CONNECTED',
          }

class TkFileDialogExample(Tkinter.Frame):
  def __init__(self, root):
    Tkinter.Frame.__init__(self, root)#, width=200, height=100)
    # options for buttons
    button_opt = {'fill': Tkconstants.BOTH, 'padx': 5, 'pady': 5}
    # define buttons
    Tkinter.Button(self, text='Open', command=self.askopenfilename).pack(**button_opt)
    # define options for opening or saving a file
    self.file_opt = options = {}
    # options['defaultextension'] = '' # couldn't figure out how this works
    options['filetypes'] = [('all files', '.*'),('XML files', '.xml'),('GML files', '.gml')]
    options['initialdir'] = 'C:\\'
    # options['initialfile'] = 'myfile.txt'
    options['parent'] = root
    options['title'] = 'Open...'
    # defining options for opening a directory
    self.dir_opt = options = {}
    options['initialdir'] = 'C:\\'
    options['mustexist'] = False
    options['parent'] = root
    options['title'] = 'This is a title'
  def askopenfilename(self):
    filename = tkFileDialog.askopenfilename(**self.file_opt)
    dothework(filename)


def dothework(filename):
  # print os.getcwd()
  # print filename
# 1. create and/or clear the tmp folder
  if not os.path.exists("tmp"):
    os.mkdir("tmp")
  else:
    shutil.rmtree("tmp")
    os.mkdir("tmp")

# 2. create and/or clear the tmp folder
  print "Processing file:", filename
  print "Parsing the file..."
  cmd = "./run.sh " + filename + " tmp"
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
  invalidsolids = 0
  xmlsolids = []
  exampleerrors = []
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
    if o.find('ERROR') != -1:
      invalidsolids += 1
      i = o.find('<errorCode>')
      while (i != -1):
        if exampleerrors.count(o[i+11:i+14]) == 0:
          exampleerrors.append(o[i+11:i+14])
        tmp = o[i+1:].find('<errorCode>')
        if tmp == -1:
          i = -1
        else:
          i = tmp + i + 1
    o = '\t<Solid>\n\t\t<id>' + solidname + '</id>\n' + o + '\t</Solid>'
    xmlsolids.append(o)

  totalxml = []
  totalxml.append('<ValidatorContext>')
  totalxml.append('\t<inputFile>' + filename + '</inputFile>')
  totalxml.append("\n".join(xmlsolids))
  totalxml.append('</ValidatorContext>')
  
  # print "\n", '-'*33, '\n '
  # print "\n".join(totalxml)
  fout = open('../report.xml', 'w')
  fout.write('\n'.join(totalxml))
  fout.close()
  print "Invalid solids: ", invalidsolids
  print "Errors present:"
  for each in exampleerrors:
    print each, dErrors[int(each)]
  

# 4. wipe the tmp folder
  os.chdir('../')
  shutil.rmtree('tmp')


if __name__ == '__main__':
  if len(sys.argv) < 2:
    print "Usage: python validate.py [-gui] infile.xml"
    sys.exit()
  if sys.argv[1] == '-gui':
    root = Tkinter.Tk()
    root.wm_title("val3dity -- validation of 3D solids")
    root.geometry('400x100')
    TkFileDialogExample(root).pack()#, width=200, height=100).pack()
    root.mainloop()
  else:
    dothework(sys.argv[1])
