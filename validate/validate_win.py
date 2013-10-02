#!/usr/bin/env python
# encoding: utf-8

### content of run.sh that should be in the path somewhere
# javac -classpath /Users/hugo/code/citygml4j-1.0-java6/lib/citygml4j.jar  *.java
# java -classpath /Users/hugo/code/citygml4j-1.0-java6/lib/citygml4j.jar:. citygml2poly $1 $2


import Tkinter, Tkconstants, tkFileDialog
import sys
import os
import subprocess
import shutil
import glob
#from lxml import etree
from StringIO import StringIO
#added for handle xml file
import xml.etree.ElementTree as MyXML

# INFILE = '/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml'
# INFILE = '/Users/hugo/Dropbox/data/citygml/os_2buildings.xml'
# INFILE = '/Users/hugo/Dropbox/data/citygml/DenHaag11Building1.xml'

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

# Terrain.clr 255 colorbaar
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
  report.xml 
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

  report_shape.xml 
    provides a visualizable results of errors found in the input model.
    Its appearance theme is "Materials for Errors"

    ''')

def dothework(filename):
# # 1. create and/or clear the tmp folder
  if not os.path.exists("tmp"):
    os.mkdir("tmp")
  else:
    shutil.rmtree("tmp")
    os.mkdir("tmp")

# 2. create and/or clear the tmp folder
  print "Processing file:", filename
  print "Parsing the file..."
  #cmd = "./run.sh " + filename + " tmp" modifed to run in windows
  cmd = ".\\run.bat " + filename + " tmp"
  subprocess.call(cmd, shell=True)
  
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
 # val3dity =   '/Users/hugo/Library/Developer/Xcode/DerivedData/val3dity-btcvseqwbnkwbueknlulczqmjyqt/Build/Products/Debug/val3dity'
  #modifed to the right folder
  val3dity = '..\\3DValidation\\3DValidation.exe'
  i = 0
  print "Number of solids in file:", len(dFiles)
  invalidsolids = 0
  xmlsolids = []
  exampleerrors = []
  for solidname in dFiles:
    # check if solid or multisurface in first file
    t = open(dFiles[solidname][0])
    t.readline()
    if t.readline().split()[1] == '0':
      multisurface = True
    else:
      multisurface = False
    t.close()
    
    # validate with val3dity
    str1 = val3dity + " -withids -xml " +  " ".join(dFiles[solidname])
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
    o = '\t<Solid>\n\t\t<id>' + solidname + '</id>\n' + o + '\t</Solid>'
    xmlsolids.append(o)

  totalxml = []
  totalxml.append('<ValidatorContext>')
  totalxml.append('\t<inputFile>' + filename + '</inputFile>')
  totalxml.append("\n".join(xmlsolids))
  totalxml.append('</ValidatorContext>')
  
  #write to the proper folder
  OutputFolder = os.path.dirname(filename)
  os.chdir(OutputFolder)

  fout = open('report.xml', 'w')
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
  # os.system("mate report.xml")

# 6 Modify the orignal file and add materials dErrors_colors to each error
  if invalidsolids != 0:
        print('Output Erroneous Map to report_shape.xml')

  #copy original file to current folder
  shutil.copyfile(filename, 'report_shape.xml')

  try:
        ReportXMLRoot = MyXML.parse('report.xml').getroot()
  except:
        print('Failed to parser report.xml')
        sys.exit()
  try:
        ModelXML = MyXML.parse('report_shape.xml')
  except:
        print('Failed to parser report_shape.xml')
        sys.exit()

  ModelXMLRoot = ModelXML.getroot()
  ModelXMLRoot.set('xmlns', 'http://www.opengis.net/citygml/1.0')

  #registrate the namespaces
  MyXML.register_namespace('app', 'http://www.opengis.net/citygml/appearance/1.0')
  MyXML.register_namespace('bldg', 'http://www.opengis.net/citygml/building/1.0')
  MyXML.register_namespace('gen', 'http://www.opengis.net/citygml/generics/1.0')
  MyXML.register_namespace('dem', 'http://www.opengis.net/citygml/relief/1.0')
  MyXML.register_namespace('gml', 'http://www.opengis.net/gml')
  MyXML.register_namespace('xAL', 'urn:oasis:names:tc:ciq:xsdschema:xAL:2.0')
  MyXML.register_namespace('xlink', 'http://www.w3.org/1999/xlink')
  MyXML.register_namespace('xsi', 'http://www.w3.org/2001/XMLSchema-instance')
  
  #add a theme for errors (tricky! for landxplorer to show the theme)
  cityObj = MyXML.SubElement(ModelXMLRoot, 'cityObjectMember')
  building = MyXML.SubElement(cityObj, '{http://www.opengis.net/citygml/building/1.0}Building')
  ErrorApp = MyXML.SubElement(building, '{http://www.opengis.net/citygml/appearance/1.0}appearance')
  ErrorApp2 = MyXML.SubElement(ErrorApp, '{http://www.opengis.net/citygml/appearance/1.0}Appearance')
  AppTheme = MyXML.SubElement(ErrorApp2, '{http://www.opengis.net/citygml/appearance/1.0}theme')
  AppTheme.text = ('Materials for Errors')
  
  #add the appearance model that indicate errors
  ErrorApp = MyXML.SubElement(ModelXMLRoot, '{http://www.opengis.net/citygml/appearance/1.0}appearance')
  AppTheme = MyXML.SubElement(ErrorApp, '{http://www.opengis.net/citygml/appearance/1.0}theme')
  AppTheme.text = ('Materials for Errors')
    
  #add x3d materials in the app:appearance according to the counted errors
  for errID in exampleerrors:
        print errID
        #add each color
        ErrorMat = MyXML.SubElement(ErrorApp, '{http://www.opengis.net/citygml/appearance/1.0}X3DMaterial')
        ErrorMat.set ('errorID', errID)
        ErrorMat.set ('errorDescription', dErrors[int(errID)])
      
        #AmbientIntensity
        MatAmbient = MyXML.SubElement(ErrorMat, '{http://www.opengis.net/citygml/appearance/1.0}ambientIntensity')
        MatAmbient.text = ('1.0')
      
        #diffuseColor
        MatDiffuse = MyXML.SubElement(ErrorMat, '{http://www.opengis.net/citygml/appearance/1.0}diffuseColor')
        curcolor = []
        for color in dErrors_colors[int(errID)].split():
            curcolor.append(str(round(float(color)/255, 3)))
        MatDiffuse.text = ' '.join(curcolor)

        #emissiveColor
        MatEmissive = MyXML.SubElement(ErrorMat, '{http://www.opengis.net/citygml/appearance/1.0}emissiveColor')
        MatEmissive.text = ' '.join(curcolor)

        #traverse all the solids in the report.xml
        for Solid in ReportXMLRoot.findall('.//Solid'):
            print Solid.find('id').text
            bNolocation = False #True indicates errors are unlocatable
            for ValidatorMessage in Solid.findall('ValidatorMessage'):
                ErrorCode = 'NULL'
                try:
                    ErrorCode = ValidatorMessage.find('errorCode').text
                except:
                    pass #no error
                if errID == ErrorCode:
                    #check wether the ID of face is valid
                    ReportFaceId = ValidatorMessage.find('face').text
                    if ReportFaceId == '-1':
                        bNolocation = True
                    else:
                        ErrorFaceId = ('#') + ValidatorMessage.find('face').text
                        #donot have to move because of the new theme
                        ##remove the original targes of the input erroneous faces
                        #for target in ModelXMLRoot.findall('.//{http://www.opengis.net/citygml/appearance/1.0}target'):
                        #    if target.text == ErrorFaceId:
                        #        #how to remove(target)?
                        #        target.text = ('')
                        #    if target.get('uri') == ErrorFaceId:
                        #        #how to remove(target)?
                        #        target.set('uri', '')
                  
                        #target to the erroneous faces
                        MatTarget = MyXML.SubElement(ErrorMat, '{http://www.opengis.net/citygml/appearance/1.0}target')
                        MatTarget.text = (ErrorFaceId)

            #if an error is unlocatable, the solid will be assign a material      
            if bNolocation:
                #if face are not reported, assign the material to the solid (works?)
                MatTarget = MyXML.SubElement(ErrorMat, '{http://www.opengis.net/citygml/appearance/1.0}target')
                MatTarget.set('targetType', 'solid')
                MatTarget.text = ('#' + Solid.find('id').text)


  ModelXML.write('report_shape.xml',xml_declaration=True, method = 'xml')

  #go back to the current folder
  os.chdir(os.getcwd())

if __name__ == '__main__':
  if len(sys.argv) < 2:
    print "Usage: python validate.py [-gui] infile.xml"
    sys.exit()
  if sys.argv[1] == '-gui':
    root = Tkinter.Tk()
    root.wm_title("val3dity -- validation of 3D solids v1.0.1beta")
    root.geometry('400x100')
    TkFileDialogExample(root).pack()#, width=200, height=100).pack()
    root.mainloop()
  else:
    dothework(sys.argv[1])
