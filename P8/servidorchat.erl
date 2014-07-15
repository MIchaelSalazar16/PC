-module(servidorchat).


-export([start/0, server/2, enviarTodos/3]).


%%empiece
start() ->
    register( miServidor, spawn(servidorchat, server, [[], 0]) ).



server_logoff(From, User_List) ->
    lists:keydelete(From, 1, User_List).



server(User_List, NumeroMensaje) ->
    receive
        {From, logon, Name} ->
            New_User_List = server_logon(From, Name, User_List, NumeroMensaje),
            server(New_User_List, NumeroMensaje);
        {From, logoff} ->
            New_User_List = server_logoff(From, User_List),
            server(New_User_List, NumeroMensaje);
        {broadcast, Message} ->
            spawn(servidorchat, enviarTodos, [User_List, Message, NumeroMensaje]),
            server(User_List, NumeroMensaje + 1)
    end.


%%nuevo usuario
server_logon(From, Name, User_List, NumeroMensaje) ->
    %% check if logged on anywhere else
    case lists:keymember(Name, 2, User_List) of
        true ->
            From ! {messenger, stop, user_exists_at_other_node},
            User_List;
        false ->
            io:format("nuevo usuario: ~p~n", [Name]),
            From ! {messenger, initialize_client_with_number, NumeroMensaje},
            [{From, Name} | User_List]
    end.



%Enviar menaje a todos
enviarTodos(User_List, Message, NumeroMensaje) ->
    io:format("Enviar a todos ~n"),
    [To_Pid ! {broadcastServidor, Message, NumeroMensaje} || {To_Pid, _ } <- User_List].
