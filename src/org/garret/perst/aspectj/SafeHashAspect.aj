package org.garret.perst.aspectj;

public aspect SafeHashAspect {

        declare precedence: PersistenceAspect+, SafeHashAspect;
        
        int around(SafeHashCode me):
                        execution(int SafeHashCode+.hashCode()) && target(me){
                return me.safeHashCode();
        }
}
 