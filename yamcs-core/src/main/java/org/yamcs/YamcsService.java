package org.yamcs;

import com.google.common.util.concurrent.Service;

/**
 * Required interface of a Yamcs Service. A Yamcs Service is a Guava service with hooks in the Yamcs configuration
 * system.
 */
public interface YamcsService extends Service {

    /**
     * Returns the valid configuration of the input args of this service.
     * 
     * @return the argument specification, or <tt>null</tt> if the args should not be validated.
     */
    public default YConfigurationSpec specifyArgs() {
        return null;
    }

    /**
     * Initialize this service. This is called before the service is started. All operations should finish fast.
     * 
     * @param yamcsInstance
     *            The yamcs instance, or <tt>null</tt> if this is a global service.
     * @param args
     *            The configured arguments for this service. If {@link #specifyArgs()} is implemented then this contains
     *            the arguments after being validated (including any defaults).
     * @throws InitException
     *             When something goes wrong during the execution of this method.
     */
    public default void init(String yamcsInstance, YConfiguration args) throws InitException {
    }
}
