package com.sumeet.qcount;

import java.io.IOException;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Hello world!
 *
 */
public class Init 
{
    public static void main( String[] args ) throws JMSException, NamingException, IOException
    {
    	new ActiveMQUtil().getAMQCount();
    }
}
