import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer

def is_even(n):
   return n%2 == 0

def rpc_server(ip, port):
   server = SimpleXMLRPCServer((ip, port))
   print "RPC server listening on %s:%d" % (ip, port)
   server.register_function(is_even, "is_even")
   server.register_function(showConnectionData, "showConnectionData")
   server.serve_forever()

