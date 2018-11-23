<p>
<h1> DMusic - <h4> Uma futura possível API para processamento e recomendação de musicas através da emoção</h4> </h1>

Este trabalho se desenvolve em 3 etapas, 1ª formação de um dataset, 2ª analisar valores extraídos da música para destacar emoções e 3ª Realizar a similaridade/recomendação das músicas.

<h1>1. Dataset</h1>
Para a formação do dataset foi escolhidas musicas em 3 playlists no Spotify. Dessas playlists 1 tinha músicas felizes e 2 com tristes. As músicas foram selecionadas de acordo com a disponibilidade de um midi (<a href="https://pt.wikipedia.org/wiki/MIDI">clique aqui para saber o que é midi</a>) da música em sites web. Após encontrado o midi, foi realizado o download das músicas em formato wav. O banco de dados com as músicas em midi e wav estão na pasta músicas deste github.

Finalizado a etapa de busca das músicas, ocorreu a escolha elementos musicais para para serem extraídos, no qual foi determinado encontrar os elementos acorde, espectograma, intervalo, modo e tom, tempo(bpm) e volume/intensidade. Cada elemento desse é encontrado utilizando bibliotecas especificas em python.

<h3>1.1 - Playlists</h3><br>
<h4>1.1.1 Playlist com músicas felizes</h4> <a href="https://open.spotify.com/user/fssanchez26/playlist/23OxyuQ1iyji14iwtKXZ9m?si=_Fxyzy7DQzKzuxIbiPWYqQ">clique aqui para ver/ouvir músicas da playlist alegria</a>

<h4>1.1.2 - Playlists com músicas tristes</h4> <a href="https://open.spotify.com/user/jelinaareyes/playlist/1GAQ7Gus83rSesb8lB6QKr?si=YhJPr_cUS2anbqHTMd2L8g">clique aqui para ver/ouvir músicas da playlist bad days</a> <br>
<a href="https://open.spotify.com/user/jelinaareyes/playlist/1GAQ7Gus83rSesb8lB6QKr?si=V19TklrWTsuxc3sySjNRvw">clique aqui para ver/ouvir músicas da playlist bad dayss😪</a>

<h3>1.2 - Elementos da Música </h3>
<h4>1.2.1 Acordes</h4> <br>
Os acordes são retirados através das uso da bibliotecas <a href="https://html.python-requests.org">HTMLSession</a> e <a href="https://docs.python.org/3/library/re.html"> re</a>. A HTMLSession faz uma requisição HTTP ao site Cifra Club para verificar a disponibilidade da cifra de uma música e também retorna todo o site HTML. A biblioteca re retira expressões como pontos e virgulas do site HTML retornado. O código para a extração dos acordes esta na pasta acordes arquivo acordes.py<br>

<h4>1.2.2 Espectograma</h4> <br>
O espectograma é um valor em heartz dado a partir da junção dos varios instrumentos. Para a extração desse valor foi usado a biblioteca <a href="http://scipy.github.io/devdocs/hacking.html">scipy</a>. Com a scipy foi possível extrair ler um áudio em wav com o môdulo wavfile, converter o valor retornado em espectograma, após isso, capturar os valores de spectro máximo e médio da música. O código para a extração do valor esta na pasta espectograma, código executa.py.

<h4>1.2.3 Intervalo</h4> <br>
Os intervalos musicais se dividem basicamente em 12 (Visto na imagem abaixo), eles representam a distância de uma nota até outra. Para a aquisição dos valores houve o uso da da biblioteca <a href="https://mido.readthedocs.io/en/latest/"> mido</a>, com este obteve-se os valores em midi das notas (<a href="https://github.com/dodo1210/tcc_code/blob/master/imagens/FWXNBXGH4AFZWE7.LARGE.jpg">Visto no presente link</a>), depois realizado o calculo da distância de uma nota até a outra. O código da captura do intervalo esta na pasta intervalo, código executa.py.
<img src="http://3.bp.blogspot.com/-WmmS1Wv6SA4/VQhwS4KJKUI/AAAAAAAACgE/Qa6OxT3AeIA/s1600/intervalos_musicais.jpg"> 

<h4>1.2.4 Modo e Tom</h4> <br>
Com o passar dos anos, a música ocidental restringiu a basicamente dois modos na música, são eles os maiores e menores. A partir do modo juntamento com o tom da música pode se saber quais são as notas ou acordes a serem tocados em uma música. Esses elementos foram extraídos com a biblioteca <a href="http://web.mit.edu/music21/"> music21</a>, no qual analisa uma música em midi e extraí retorna esses valores a partir de um função. O código de ambos os alementos está na pasta tom e no arquivo executa.py.

<h4>1.2.5 Tempo</h4> <br>
O tempo de uma música pode ser representado a partir de um exemplo. Pense em uma música dançante, agora pense em um coreográfo falando 1,2,3,4. Bom, o tempo de uma música é representado através do tempo que leva do 1 para o 2, do 2 para o 3, assim por diante. Esse valor é extraído pela biblioteca <a href="http://librosa.github.io/librosa/tutorial.html">librosa</a>, que analisa a música e retorna alguns valores que representam a mesma. Por fim usa-se uma a função beat para capturar o tempo. O código para aquisição do tempo esta na pasta tempo arquivo executa.py.

<h4>1.2.6 Volume/Intensidade</h4> <br>
O volume ou a intesidade da música é um elemento que representa o quanto é vibrante, ou melancólica pode ser uma música. Neste trabalho foi extraído a intensidade média da música com a biblioteca <a href="https://github.com/jiaaro/pydub"> pydub</a>. A intensidade da música é representada em decibéis. O código para este elemente esta na pasta volume, arquivo executa.py.# tcc_code
