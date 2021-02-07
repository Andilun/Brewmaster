# Brewmaster
Fermentation temperature control for mini fridge controlled rpi, smart plug and termostat.

#Example of a setting file

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
