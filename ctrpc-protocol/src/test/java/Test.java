import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.request.RpcRequest;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class Test {
    public static RpcProtocol<RpcRequest> getProtocol(){
        RpcHeader header = RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType());
        RpcRequest body = new RpcRequest();
        body.setOneway(false);
        body.setAsync(false);
        body.setClassName("com.ct.rpc.demo.RpcProtocol");
        body.setMethodName("hello");
        body.setGroup("ct");
        body.setParameters(new Object[]{"ct"});
        body.setParameterTypes(new Class[]{String.class});
        body.setVersion("1.0.0");
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setBody(body);
        protocol.setHeader(header);
        return protocol;
    }
}
