import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)

trig = 23 #23
echo = 24 #24
led_g = 17
led_y = 27
led_r = 22


print "start"

gpio.setup(trig, gpio.OUT)
gpio.setup(echo, gpio.IN)
gpio.setup(led_g, gpio.OUT)
gpio.setup(led_y, gpio.OUT)
gpio.setup(led_r, gpio.OUT)

try :
    i = 0
    dis_arr = [10,10,10,10,10,10,10,10,10,10] 
    while True:
        gpio.output(trig, False)
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

        dis_arr[i] = distance
        print "Array : ", dis_arr[i]
        print " i vlaue : ", i

        if dis_arr[i] > 15 :
            gpio.output(led_r, gpio.HIGH)
            gpio.output(led_y, gpio.LOW)
            gpio.output(led_g, gpio.LOW)
        if dis_arr[i] < 10 :
            gpio.output(led_r, gpio.LOW)
            gpio.output(led_y, gpio.HIGH)
            gpio.output(led_g, gpio.LOW)
        if i > 1 and dis_arr[i] < 10 and dis_arr[i-1] < 10 and dis_arr[i-2] < 10:
            gpio.output(led_r, gpio.LOW)
            gpio.output(led_y, gpio.LOW)
            gpio.output(led_g, gpio.HIGH)
            print " dddddddddddddddddddddddddddddddddddddddddddddddd"
        if i == 9:
            dis_arr[2] = dis_arr[i]
            dis_arr[1] = dis_arr[i-1]
            dis_arr[0] = dis_arr[i-2]
            print "Array : ", dis_arr[2]
            print "Array : ", dis_arr[1]
            print "Array : ", dis_arr[0]
            i = 2
        
        i = i + 1
        
except KeyboardInterrupt:
    gpio.cleanup()
