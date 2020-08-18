# server.py : 연결해 1 보낼 수 있음.
import socket
import time
import serial
ser = serial.Serial("COM4", 9600, timeout = 1)

host = '10.1.4.111' # 호스트 ip를 적어주세요
port = 8080            # 포트번호를 임의로 설정해주세요

server_sock = socket.socket(socket.AF_INET)
server_sock.bind((host, port))
server_sock.listen(1)

print("기다리는 중..")

#data = client_sock.recv(1024)
#print(data.decode("utf-8"), len(data))
out_data = int(10)

while True:
    client_sock, addr = server_sock.accept()

    if client_sock:
        print('Connected by?!', addr)
        in_data = client_sock.recv(1024)
        print('rcv :', in_data.decode("utf-8"), len(in_data))
        while in_data:
            print("R: ", ser.readline())
            client_sock.send(str(out_data).encode("utf-8"))
            print('send :', out_data)
            #out_data = out_data+1
            time.sleep(2)


client_sock.close()
server_sock.close()