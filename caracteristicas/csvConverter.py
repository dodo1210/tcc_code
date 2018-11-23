from xml.dom import minidom

doc = minidom.parse("/home/douglas/Documentos/tcc_code/programas/processamento_de_audio_java/jAudio/feature_values_1.xml")
# doc.getElementsByTagName returns NodeList
name = doc.getElementsByTagName("v")
for i in name:
    print(i.firstChild.data)

'''value = doc.getElementsByTagName("v")
for i in value:
    print(i.firstChild.data)
'''