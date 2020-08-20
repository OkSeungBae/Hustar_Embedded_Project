import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)

trig = 23
echo = 24
led_g = 21
led_y = 20
led_r = 16


print "start"

gpio.setup(trig, gpio.OUT)
gpio.setup(echo, gpio.IN)
gpio.setup(led_g, gpio.OUT)
gpio.setup(led_y, gpio.OUT)
gpio.setup(led_r, gpio.OUT)

try :
    i = 0
    
    while True:s2131output(trig, False)
        time.sleep(0.5)
                
        gpio.output(trig,True)
        time.sleep(0.00001)
        gpio.output(trig,False)

        while gpio.input(echo) == 0:
            pulse_start = time.time()

        while gpio.input(echo) == 1:
            pulse_end = time.time()

        pulse_duration = pulse_end - pulse_start
        distance = pulse_duration*17000
        distance = round(distance, 2)

        print "Distance : ", distance, "cm"


        if distance < 20 and distance > 10 :
            gpio.output(led_r, gpio.HIGH)
            gpio.output(led_y, gpio.LOW)
            gpio.output(led_g, gpio.LOW)
        if distance <= 10 and distance > 5 :
            gpio.output(led_r, gpio.LOW)
            gpio.output(led_y, gpio.HIGH)
            gpio.output(led_g, gpio.LOW)
        if distance <= 5 :
            gpio.output(led_r, gpio.LOW)
            gpio.output(led_y, gpio.LOW)
            gpio.output(led_g, gpio.HIGH)
        
        
except KeyboardInterrupt:
    gpio.cleanup()

