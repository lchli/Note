package com.lch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.concurrent.ConcurrentHashMap;

public class MyAopVisitor {

    public byte[] modifyClassBytes(byte[] source) {
        ClassReader classReader = new ClassReader(source);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new TraceClassAdapter(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private class TraceClassAdapter extends ClassVisitor {

        private String className;
        private String superName;
        private boolean isABSClass = false;
        private boolean hasWindowFocusMethod = false;
        private boolean isActivityOrSubClass;
        private boolean isNeedTrace = true;

        TraceClassAdapter(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
            this.superName = superName;
            // this.isActivityOrSubClass = isActivityOrSubClass(className, collectedClassExtendMap);
            // this.isNeedTrace = MethodCollector.isNeedTrace(configuration, className, mappingCollector);
            if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
                this.isABSClass = true;
            }

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {

            if (isABSClass) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            } else {
                MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                return new TraceMethodAdapter(api, methodVisitor, access, name, desc, this.className,
                        hasWindowFocusMethod, isActivityOrSubClass, isNeedTrace);
            }
        }


        @Override
        public void visitEnd() {

            super.visitEnd();
        }
    }

    private class TraceMethodAdapter extends AdviceAdapter {

        private final String methodName;
        private final String name;
        private final String className;
        private final boolean hasWindowFocusMethod;
        private final boolean isNeedTrace;
        private final boolean isActivityOrSubClass;

        protected TraceMethodAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className,
                                     boolean hasWindowFocusMethod, boolean isActivityOrSubClass, boolean isNeedTrace) {
            super(api, mv, access, name, desc);
            //TraceMethod traceMethod = TraceMethod.create(0, access, className, name, desc);
            this.methodName = name;// traceMethod.getMethodName();
            this.hasWindowFocusMethod = hasWindowFocusMethod;
            this.className = className;
            this.name = name;
            this.isActivityOrSubClass = isActivityOrSubClass;
            this.isNeedTrace = isNeedTrace;

        }

        @Override
        protected void onMethodEnter() {
            // TraceMethod traceMethod = collectedMethodMap.get(methodName);
            // if (traceMethod != null) {
            // traceMethodCount.incrementAndGet();
            mv.visitLdcInsn(0);
            mv.visitMethodInsn(INVOKESTATIC, TraceBuildConstants.MATRIX_TRACE_CLASS, "i", "(I)V", false);

//                if (checkNeedTraceWindowFocusChangeMethod(traceMethod)) {
//                    traceWindowFocusChangeMethod(mv, className);
//                }
            //  }
        }


        @Override
        protected void onMethodExit(int opcode) {
            // TraceMethod traceMethod = collectedMethodMap.get(methodName);
            //if (traceMethod != null) {
            // traceMethodCount.incrementAndGet();
            mv.visitLdcInsn(0);
            mv.visitMethodInsn(INVOKESTATIC, TraceBuildConstants.MATRIX_TRACE_CLASS, "o", "(I)V", false);
            // }
        }


    }

}
