#!/usr/bin/env python
# encoding: utf-8

import Tkinter, Tkconstants, tkFileDialog
import sys
import os
import subprocess
import shutil
import glob
sys.path.append("obj2poly/")
from ConvProvider import ConvProvider
#from lxml import etree
from StringIO import StringIO
#added for handle xml file
import xml.etree.ElementTree as MyXML

dErrors = {
          100: 'DUPLICATE_POINTS',
          110: 'RING_NOT_CLOSED',

          200: 'INNER_RING_WRONG_ORIENTATION',
          210: 'NON_PLANAR_SURFACE',
          211: 'DEGENERATE_SURFACE',
          220: 'SURFACE_PROJECTION_INVALID',
          221: 'INNER_RING_INTERSECTS_OUTER',
          222: 'INNER_RING_OUTSIDE_OUTER',
          223: 'INNER_OUTER_RINGS_INTERSECT',
          224: 'INTERIOR_OF_RING_NOT_CONNECTED',
          230: 'NON_SIMPLE_SURFACE',

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

#
dErrors_colors = {
          100: '200 215 133',
          110: '171 217 177',

          200: '124 196 120',
          210: '117 193 120',
          211: '255 0 0',
          220: '219 208  78',
          221: '241 207  14',
          222: '242 167   0',
          223: '192 159  13',
          224: '211 192 112',
          230: '0 255 0',

          300: '175 204 166',
          301: '240 219 161',
          302: '252 236 192',
          303: '248 250 234',
          304: '229 254 250',
          305: '219 255 253',
          306: '214 251 252',
          310: '0 0 255',

          400: '115 224 241',
          410: '29 188 239',
          420: '0 137 245',
          430: '183 244 247', 
          }

class TkFileDialogExample(Tkinter.Frame):
  def __init__(self, root):
    Tkinter.Frame.__init__(self, root)#, width=200, height=100)
    # options for buttons
    button_opt = {'fill': Tkconstants.BOTH, 'padx': 5, 'pady': 5}
    # define buttons
    Tkinter.Button(self, text='Open', command=self.askopenfilename).pack(**button_opt)
    Tkinter.Button(self, text='About', command=self.About).pack(**button_opt)
    # define options for opening or saving a file
    self.file_opt = options = {}
    # options['defaultextension'] = '' # couldn't figure out how this works
    options['filetypes'] = [('all files', '.*'),('obj files', '.obj')]
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
    os.system( 'cls' )
    filename = tkFileDialog.askopenfilename(**self.file_opt)
    dothework(filename)
  def About(self):
    print('''
3D VALIDATOR FOR CITYGML MODELS

-Version-
    V1.0.1beta

-Description-
    This is a tool for validating geometry of CityGML models.
    It supports the CityGML(2.0) datasets and is ISO19107 conforming.
    The source code are avaliable at
    http://code.google.com/p/citygml2poly/
    http://code.google.com/p/val3dity/

-Results- 
  repair_.xml 
    describes the validition results of solids in the model.
    The error codes and their semantics are:
    100: 'DUPLICATE_POINTS',
    110: 'RING_NOT_CLOSED',

    200: 'INNER_RING_WRONG_ORIENTATION',
    210: 'NON_PLANAR_SURFACE',
    211: 'DEGENERATE_SURFACE',
    220: 'SURFACE_PROJECTION_INVALID',
    221: 'INNER_RING_INTERSECTS_OUTER',
    222: 'INNER_RING_OUTSIDE_OUTER',
    223: 'INNER_OUTER_RINGS_INTERSECT',
    224: 'INTERIOR_OF_RING_NOT_CONNECTED',
    230: 'NON_SIMPLE_SURFACE',

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

  repair_shape_.xml 
    provides a visualizable results of errors found in the input model.
    Its appearance theme is "Materials for Errors"

    ''')

def dothework(filename):
  filename = str(filename)
# 0. define the output path
  fREPORT = 'report_' + os.path.splitext(os.path.basename(filename))[0] + '.xml'
  fREPORT_SHAPE_TMP = 'report_shape_' + os.path.splitext(os.path.basename(filename))[0]
  fREPORT_SHAPE = fREPORT_SHAPE_TMP + '.obj'
  fREPORT_SHAPEMTL =  fREPORT_SHAPE_TMP + ".mtl"
  fREPORT_SHAPE_TMP = fREPORT_SHAPE_TMP + 'tmp.obj'

# 1. convert obj to poly
  MyCov = ConvProvider()
  polyfile = filename[:-4] + ".poly"
  if MyCov.convert(filename, polyfile, False) == False:
        print("conversion failed")
        sys.os.exit(0)
  else:
        print("output", polyfile)

  val3dity = '3DValidation\\3DValidation.exe'
  invalidsolids = 0
  xmlsolids = []
  exampleerrors = []
    # check if solid or multisurface in first file
  t = open(polyfile)
  t.readline()
  if t.readline().split()[1] == '0':
      multisurface = True
  else:
      multisurface = False
  t.close()
    
    # validate with val3dity
  str1 = val3dity + " -xml " +  "".join(polyfile)
  op = subprocess.Popen(str1.split(' '), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
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
  else: #-- no error detected, WARNING if MultiSurface!
      if multisurface == True:
        print 'WARNING: MultiSurfce is actually a valid solid'
        s = []
        s.append("\t\t<ValidatorMessage>")
        s.append("\t\t\t<type>WARNING</type>")
        s.append("\t\t\t<explanation>MultiSurfaces form a valid Solid</explanation>")
        s.append("\t\t</ValidatorMessage>\n")
        o = "\n".join(s)
  o = '\t<Solid>\n\t\t<id>' + polyfile + '</id>\n' + o + '\t</Solid>'
  xmlsolids.append(o)

  totalxml = []
  totalxml.append('<ValidatorContext>')
  totalxml.append('\t<inputFile>' + filename + '</inputFile>')
  totalxml.append("\n".join(xmlsolids))
  totalxml.append('</ValidatorContext>')
  
  #write to the proper folder
  OutputFolder = os.path.dirname(filename)
  os.chdir(OutputFolder)

  fout = open(fREPORT, 'w')
  fout.write('\n'.join(totalxml))
  fout.close()
  print "Invalid solids: ", invalidsolids
  print "Errors present:"
  for each in exampleerrors:
    print each, dErrors[int(each)]


# 4. do not wipe the tmp folder
#  os.chdir('../')
#  shutil.rmtree('tmp')

# 5. open textmate
  # os.system("mate " + fREPORT)

#6 Modify the orignal file and add materials dErrors_colors to each error
  if invalidsolids != 0:
        print('Output Erroneous Map to ' + fREPORT_SHAPE)

  ##copy original file to current folder
  #convert poly to obj
  #polyfile = filename[:-4] + ".poly"
  if MyCov.convert(polyfile, fREPORT_SHAPE_TMP, False) == False:
        print("conversion failed")
        sys.os.exit(0)
  try:
        f_shape_tmp = file(fREPORT_SHAPE_TMP, 'r')
  except:
        print('invalid file:' + fREPORT_SHAPE_TMP)
        sys.exit()

  try:
        ReportXMLRoot = MyXML.parse(fREPORT).getroot()
  except:
        print('Failed to parser ' + fREPORT)
        sys.exit()
  try:
        f_shape = file(fREPORT_SHAPE, 'w')
  except:
        print('invalid file:' + fREPORT_SHAPE)
        sys.exit()
  try:
        f_mtl = file(fREPORT_SHAPEMTL,'w')
  except:
        print ("invalid file: " + fREPORT_SHAPEMTL)
        return

  f_shape.write('mtllib ' + fREPORT_SHAPEMTL + '\n')
  #print errid
  f_mtl.write('newmtl manifold'+ '\n')
  f_mtl.write('Ka 0.5 0.5 0.5'+'\n')
  f_mtl.write('Kd 0.5 0.5 0.5'+'\n')
  f_mtl.write('Ks 0.0 0.0 0.0'+'\n')
  ##add x3d materials in the app:appearance according to the counted errors
  errorlist = ({})
  for errid in exampleerrors:
        #print errid
        f_mtl.write('newmtl ' + str(errid) + '\n')
        curcolor = []
        for color in dErrors_colors[int(errid)].split():
            curcolor.append(str(round(float(color)/255, 3)))

        f_mtl.write('Ka '+ curcolor[0] + ' ' + curcolor[1] + ' ' + curcolor[2]+ '\n')
        f_mtl.write('Kd '+ curcolor[0] + ' ' + curcolor[1] + ' ' + curcolor[2]+ '\n')
        f_mtl.write('Ks '+ curcolor[0] + ' ' + curcolor[1] + ' ' + curcolor[2]+ '\n')
      
        #traverse all the solids in the freport
        for solid in ReportXMLRoot.findall('.//Solid'):
            #print solid.find('id').text
            for validatormessage in solid.findall('ValidatorMessage'):
                errorcode = 'null'
                try:
                    errorcode = validatormessage.find('errorCode').text
                except:
                    pass #no error
                if errid == errorcode:
                    #check wether the id of face is valid
                    reportfaceid = validatormessage.find('face').text
                    if reportfaceid == '-1':
                        #if an error is unlocatable
                        pass
                    else:
                        errorlist[int(reportfaceid)] = errid

  #add mtl to the face in the fREPORT_SHAPE
  icount = 0
  for line in f_shape_tmp:
    bTag = False
    if line.startswith('f') and line[1] == ' ':
        try:
            errid = errorlist[icount]
            f_shape.write('usemtl ' + errid + '\n')
            bTag = True
        except:
            pass
        icount = icount + 1

    f_shape.write(line)
    if bTag:
        f_shape.write('usemtl manifold\n')
    

  f_mtl.close()
  f_shape.close()
  f_shape_tmp.close()
  os.remove(fREPORT_SHAPE_TMP)
  os.remove(polyfile)
  
  print('Finished!')

if __name__ == '__main__':
  if len(sys.argv) < 2:
    print "Usage: python validate_obj.py [-gui] infile.obj"
    sys.exit()
  if sys.argv[1] == '-gui':
    root = Tkinter.Tk()
    root.wm_title("val3dity -- validation of 3D solids")
    root.geometry('400x100')
    TkFileDialogExample(root).pack()#, width=200, height=100).pack()
    root.mainloop()
  else:
    dothework(sys.argv[1])
