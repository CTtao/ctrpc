package com.ct.rpc.test.scanner;

import com.ct.rpc.common.scanner.ClassScanner;
import com.ct.rpc.common.scanner.reference.RpcReferenceScanner;
import com.ct.rpc.common.scanner.server.RpcServiceScanner;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ScannerTest {
    public static void main(String[] args) throws Exception{
        ScannerTest scannerTest = new ScannerTest();
        scannerTest.testScannerClassNameListByRpcReference();
    }

    /**
     * 扫描com.ct.rpc.test.scanner包下的所有类
     * @throws Exception
     */
    public void testScannerClassNameList() throws Exception{
        List<String> classNameList = ClassScanner.getClassNameList("com.ct.rpc.test.scanner");
        classNameList.forEach(System.out::println);
    }

    /**
     * 扫描com.ct.rpc.test.scanner包下所有标注了@RpcService注解的类
     * @throws Exception
     */
    public void testScannerClassNameListByRpcService() throws Exception{
        RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.ct.rpc.test.scanner");
    }

    public void testScannerClassNameListByRpcReference() throws Exception{
        RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("com.ct.rpc.test.scanner");
    }

}
