import xmlrpclib

proxy = xmlrpclib.ServerProxy("http://192.168.222.129:8022/")
#print "3 is even: %s" % str(proxy.is_even(3))
#print "100 is even: %s" % str(proxy.is_even(100))
print proxy.getConnectionData()
