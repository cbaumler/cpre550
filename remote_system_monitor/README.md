#Remote System Monitoring using RPC

##Description

The system under consideration consists of a network of Linux workstations. The objective is to design a Network Management Application (NMA) that provides statistics gathered from the various workstations for the purpose of managing the network. The application will not perform statistical analysis or corrective actions. However, this functionality could be added in the future.

##Requirements

The Network Management Application (NMA) shall be available on every workstation in the network.

The NMA shall contain a client and a server component.

The NMA client shall accept as an input the name of the remote host for which statistics are being requested.

The NMA client shall provide a menu of options for the user to select a statistic to be tracked in real time.

When option 1 is selected, the NMA client shall track the current system time and date for the specified remote host.

When option 2 is selected, the NMA client shall track the statistics provided by the Linux “top” utility for the specified remote host.

When option 3 is selected, the NMA client shall track the statistics provided by the Linux “netstat” utility for the specified remote host.

When option 4 is selected, the NMA client shall exit.

The NMA client shall use remote procedure call (RPC) to request data from the NMA server.

The NMA server shall use RPC to return requested data to the NMA client.

##Design Description

**Client:** This component is responsible for collecting various system statistics from the other workstations in the network. It accepts the name of a remote host when it is executed and provides the user with a menu of options for selecting statistics to retrieve from the host. When a statistic is selected, the client uses RPC to request data from the host. This data is printed to the console for viewing.

**Server:** This component listens for requests from the client. When a request is received, the server retrieves the requested data and sends it back to the client. Requests and responses are handled using RPC.            

**RPC:** The program “rpcgen” is used to generate the RPC code used in this system. This is accomplished by specifying an interface (nma.x) that includes a program number and a version number. The interface also defines a single function called “get_sys_info” which accepts as input a long data type that specifies the user selected menu option. The function returns a pointer to a buffer containing the response data from the server.
