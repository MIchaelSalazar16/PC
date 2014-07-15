%%% Message passing utility.
%%% User interface:
%%% logon(Name)
%%%     One user at a time can log in from each Erlang node in the
%%%     system messenger: and choose a suitable Name. If the Name
%%%     is already logged in at another node or if someone else is
%%%     already logged in at the same node, login will be rejected
%%%     with a suitable error message.
%%% logoff()
%%%     Logs off anybody at at node
%%% message(ToName, Message)
%%%     sends Message to ToName. Error messages if the user of this
%%%     function is not logged on or if ToName is not logged on at
%%%     any node.
%%%
%%% One node in the network of Erlang nodes runs a server which maintains
%%% data about the logged on users. The server is registered as "messenger"
%%% Each node where there is a user logged on runs a client process registered
%%% as "mess_client"
%%%
%%% Protocol between the client processes and the server
%%% ----------------------------------------------------
%%%
%%% To server: {ClientPid, logon, UserName}
%%% Reply {messenger, stop, user_exists_at_other_node} stops the client
%%% Reply {messenger, logged_on} logon was successful
%%%
%%% To server: {ClientPid, logoff}
%%% Reply: {messenger, logged_off}
%%%
%%% To server: {ClientPid, logoff}
%%% Reply: no reply
%%%
%%% To server: {ClientPid, message_to, ToName, Message} send a message
%%% Reply: {messenger, stop, you_are_not_logged_on} stops the client
%%% Reply: {messenger, receiver_not_found} no user with this name logged on
%%% Reply: {messenger, sent} Message has been sent (but no guarantee)
%%%
%%% To client: {message_from, Name, Message},
%%%
%%% Protocol between the "commands" and the client
%%% ----------------------------------------------
%%%
%%% Started: messenger:client(Server_Node, Name)
%%% To client: logoff
%%% To client: {message_to, ToName, Message}
%%%
%%% Configuration: change the server_node() function to return the
%%% name of the node where the messenger server runs




-module(clienteschat).


-export([login/1, logoff/0, message/1, clienteEmpezar/2]).


server_node() ->
    'server@antoniovirtualBox'.


%% nuevo cliente
login(Nombre) ->
    case whereis(mess_client) of
        undefined ->
            register(mess_client, spawn(clienteschat, clienteEmpezar, [server_node(), Nombre]));
        _ -> already_logged_on
    end.


%% desconectarse
logoff() ->
    mess_client ! logoff.


%% enviar mensaje
message(Message) ->
    case whereis(mess_client) of
        undefined ->
            not_logged_on;
        _ -> mess_client ! {message_to, Message}
    end.


%% emepzar el cliente viene de client de la 0
clienteEmpezar(Server_Node, Name) ->
    {miServidor, Server_Node} ! {self(), logon, Name},
    await_result(Server_Node).


%  esperando
await_result(Server_Node) ->
    receive
        {messenger, stop, Why} ->
            io:format("para ~p~n", [Why]),
            exit(normal);
        {messenger, initialize_client_with_number, Number} ->
        io:format("cliente #~p~n", [Number]),
        client(Server_Node, Number)
    end.


client(Server_Node, Numero) ->
    receive
        logoff ->
            {miServidor, Server_Node} ! {self(), logoff},
            exit(normal);
        {message_to, Message} ->
            {miServidor, Server_Node} ! {broadcast, Message},
            client(Server_Node, Numero);
        {broadcastServidor, Message, NumeroMensaje} ->
            if
                NumeroMensaje < Numero ->
                    client(Server_Node, Numero);
                NumeroMensaje == Numero ->
                    io:format("Message from ~p: ~p~n", [Numero, Message]),
                    client(Server_Node, Numero + 1)
            end
    end.


