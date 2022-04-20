package com.lch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MyAopVisitor {
    private final List<String> collectedIgnoreMethod = new ArrayList<>();

    public byte[] modifyClassBytes(byte[] source) {
        ClassReader classReader = new ClassReader(source);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new FindMethodInfoVisitor(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
       // source = classWriter.toByteArray();

        System.err.println("collectedIgnoreMethod:"+collectedIgnoreMethod);


        classReader = new ClassReader(source);
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classVisitor = new TraceClassAdapter(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private static boolean isIgnoreClass(String classname) {

        return classname.contains("AppMethodBeat");
    }

    private class FindMethodInfoVisitor extends ClassVisitor {
        private String className;
        private String superName;
        private boolean isABSClass = false;


        FindMethodInfoVisitor(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }


        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
            this.superName = superName;
            if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
                this.isABSClass = true;
            }

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {

            if (isIgnoreClass(className)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if (isABSClass) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            return new CollectMethodNode(className, access, name, desc, signature, exceptions);

        }


        private class CollectMethodNode extends MethodNode {
            private String className;
            private boolean isConstructor;


            CollectMethodNode(String className, int access, String name, String desc,
                              String signature, String[] exceptions) {
                super(Opcodes.ASM5, access, name, desc, signature, exceptions);
                this.className = className;
            }

            @Override
            public void visitEnd() {
                super.visitEnd();

                if ("<init>".equals(name)) {
                    isConstructor = true;
                }
                // filter simple methods
                if ((isEmptyMethod() || isGetSetMethod() || isSingleMethod())) {
                    collectedIgnoreMethod.add(name + "-" + desc);
                    return;
                }

            }

            private boolean isGetSetMethod() {
                int ignoreCount = 0;
                ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    int opcode = insnNode.getOpcode();
                    if (-1 == opcode) {
                        continue;
                    }
                    if (opcode != Opcodes.GETFIELD
                            && opcode != Opcodes.GETSTATIC
                            && opcode != Opcodes.H_GETFIELD
                            && opcode != Opcodes.H_GETSTATIC

                            && opcode != Opcodes.RETURN
                            && opcode != Opcodes.ARETURN
                            && opcode != Opcodes.DRETURN
                            && opcode != Opcodes.FRETURN
                            && opcode != Opcodes.LRETURN
                            && opcode != Opcodes.IRETURN

                            && opcode != Opcodes.PUTFIELD
                            && opcode != Opcodes.PUTSTATIC
                            && opcode != Opcodes.H_PUTFIELD
                            && opcode != Opcodes.H_PUTSTATIC
                            && opcode > Opcodes.SALOAD) {
                        if (isConstructor && opcode == Opcodes.INVOKESPECIAL) {
                            ignoreCount++;
                            if (ignoreCount > 1) {
                                return false;
                            }
                            continue;
                        }
                        return false;
                    }
                }
                return true;
            }

            private boolean isSingleMethod() {
                ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    int opcode = insnNode.getOpcode();
                    if (-1 == opcode) {
                        continue;
                    } else if (Opcodes.INVOKEVIRTUAL <= opcode && opcode <= Opcodes.INVOKEDYNAMIC) {
                        return false;
                    }
                }
                return true;
            }


            private boolean isEmptyMethod() {
                ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insnNode = iterator.next();
                    int opcode = insnNode.getOpcode();
                    if (-1 == opcode) {
                        continue;
                    } else {
                        return false;
                    }
                }
                return true;
            }

        }
    }

    private class TraceClassAdapter extends ClassVisitor {

        private String className;
        private String superName;
        private boolean isABSClass = false;

        TraceClassAdapter(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
            this.superName = superName;
            if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
                this.isABSClass = true;
            }

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {

            if (isIgnoreClass(className)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if (isABSClass) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }


            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
            return new TraceMethodAdapter(api, methodVisitor, access, name, desc,className);

        }


    }

    private class TraceMethodAdapter extends AdviceAdapter {


        private final String classname;

        protected TraceMethodAdapter(int api, MethodVisitor mv, int access, String name, String desc, String classname) {
            super(api, mv, access, name, desc);
            this.classname=classname;

        }

        @Override
        protected void onMethodEnter() {
            if (collectedIgnoreMethod.contains(getName() + "-" + methodDesc)) {
                return;
            }
            mv.visitLdcInsn(0);
            mv.visitMethodInsn(INVOKESTATIC, TraceBuildConstants.MATRIX_TRACE_CLASS, "i", "(I)V", false);
        }


        @Override
        protected void onMethodExit(int opcode) {
            if (collectedIgnoreMethod.contains(getName() + "-" + methodDesc)) {
                return;
            }

            mv.visitLdcInsn(classname);
            mv.visitLdcInsn(getName());
            mv.visitLdcInsn(methodDesc);
            mv.visitMethodInsn(INVOKESTATIC, TraceBuildConstants.MATRIX_TRACE_CLASS, "o", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        }


    }

}
