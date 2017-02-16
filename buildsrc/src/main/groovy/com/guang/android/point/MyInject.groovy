package com.guang.android.point

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod


public class MyInject {

    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"start point!!\" ); ";

    public static void injectDir(String path,String packageName,String sdkDir){

        pool.appendClassPath(path)
        pool.appendClassPath(sdkDir + "/platforms/android-25/android.jar")


        File dir = new File(path)
        if (dir.isDirectory()){
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                System.out.println(filePath);

                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {

                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;

                    if (isMyPackage){
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        //开始修改class文件
                        CtClass c = pool.getCtClass(className)

                        if (c.isFrozen()) {
                            c.defrost()
                        }

                        CtMethod[] mtds = c.getMethods()

                        for (int i = 0; i < mtds.length; i++) {
                            CtMethod mtd = mtds[i]
                            System.out.println("********************")
                            System.out.println(mtd.getName())
                            CtClass[] ctcs = mtd.getParameterTypes()

                            ctcs.each { CtClass ctClass ->
                                System.out.println(ctClass.getName())
                            }
                        }


                        CtConstructor[] cts = c.getDeclaredConstructors()
                        pool.importPackage("android.util.Log");
                        if (cts == null || cts.length == 0) {
                            //手动创建一个构造函数
                            CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                            constructor.insertBeforeBody(injectStr)
                            c.addConstructor(constructor)
                        } else {
                            cts[0].insertBeforeBody(injectStr)
                        }
                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
        }
    }
}