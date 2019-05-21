package uk.ac.imperial.cisbio.cell_metric.utilities;

import matlabcontrol.*;


public class MatlabWrapper {
	
	public static void runMatlab(String image,String outputImage) throws MatlabConnectionException, MatlabInvocationException {
	         // create proxy
	         MatlabProxyFactoryOptions options =
	            new MatlabProxyFactoryOptions.Builder()
	                .setUsePreviouslyControlledSession(true)
	                .build();
	        MatlabProxyFactory factory = new MatlabProxyFactory(options);
	        MatlabProxy proxy = factory.getProxy();
	        
	        // call builtin function
	        String imread="y = im2double(imread('"+image+"'));";
	        proxy.eval("path(path,'C:/Data/vania_braga/new_project/BM3D');");
	        proxy.eval(imread);
	        proxy.eval("randn('seed', 0);");
	        proxy.eval("sigma = 25;");
	        proxy.eval("z = y + (sigma/255)*randn(size(y));");
	        proxy.eval(" [NA, y_est] = BM3D(y, z, sigma);");
	        String imWrite="imwrite(y_est,'"+outputImage+"');";
	        System.out.println(imWrite);
	        proxy.eval(imWrite);
	        
	        
	        // close connection
	        proxy.disconnect();
	    }


}
