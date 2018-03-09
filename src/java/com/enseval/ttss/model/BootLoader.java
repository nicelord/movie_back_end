package com.enseval.ttss.model;

import javax.servlet.http.*;
import org.avaje.agentloader.*;
import javax.servlet.*;

public class BootLoader extends HttpServlet
{
    public void init() throws ServletException {
        AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1");
        System.out.println("----------");
        System.out.println("---------- BootLoader Servlet Initialized successfully ----------");
        System.out.println("----------");
    }
}
