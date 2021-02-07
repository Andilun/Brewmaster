# Brewmaster
Fermentation temperature control for mini fridge controlled by rpi, smart plug and termostat.<br> 
To run the programm as is you need a DS18B20 installed/plugged in on your rpi
,python<br> and python-kasa. See https://github.com/python-kasa/python-kasa for more information.

#Example of a setting file\
(\ is used for formatting and should be removed in a real settings file)
--------------------------
#settings\
LogFileName=brew.log\
CoolerState=1\
HeaterState=0\
HasHeater=0\
DiffPlus=0.5\
DiffMinus=0.5\
CoolerIp=192.168.2.52\
HeaterIp=0\
LogFreq=1\
Onoffdelay=15\
#days;temp\
!state=Ferment\
14;12\
!state=Diactyl rest\
3;16.66\
!state=Lager cooldown\
1;13.89\
1;11.11\
1;8.33\
1;5.56\
1;2.78\
!state=Lager\
42;1.67

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
