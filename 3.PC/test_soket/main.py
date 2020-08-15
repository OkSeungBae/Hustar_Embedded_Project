# server.py : 연결해 1 보낼 수 있음.
import socket
import time

host = '192.168.0.3' # 호스트 ip를 적어주세요
port = 8080            # 포트번호를 임의로 설정해주세요

server_sock = socket.socket(socket.AF_INET)
server_sock.bind((host, port))
server_sock.listen(1)

print("기다리는 중")

#data = client_sock.recv(1024)
#print(data.decode("utf-8"), len(data))
out_data = int(10)

while True:
    client_sock, addr = server_sock.accept()

    if client_sock:
        print('Connected by', addr)
        in_data = client_sock.recv(1024)
        print('rcv :', in_data.decode("utf-8"), len(in_data))
        client_sock.send(out_data.to_bytes(4, byteorder='little'))
        print('send :', out_data)
        out_data = out_data+1


client_sock.close()
server_sock.close()