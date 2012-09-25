import os
import glob
import subprocess
from lxml import etree
from StringIO import StringIO

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

val3dity =  '/Users/hugo/Library/Developer/Xcode/DerivedData/val3dity-btcvseqwbnkwbueknlulczqmjyqt/Build/Products/Debug/val3dity'  

# os.chdir('/Users/hugo/projects/citygml2poly/tmp_dh/')
os.chdir('/Users/hugo/Dropbox/data/validation/IMGeo-test/LOD2/')

# for f in os.listdir('.'):
#   if f[-4:] == 'poly':
#     print "-"*20
#     print f
#     cmd = val3dity + " -withids -xml " +  f
#     os.system(cmd)
#     print "-"*20


# 3. validate each building/shell
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
totalxml.append('\t<inputFile>' + 'filename' + '</inputFile>')
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