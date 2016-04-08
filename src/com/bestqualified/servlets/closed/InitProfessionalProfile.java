package com.bestqualified.servlets.closed;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitProfessionalProfile extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4737209875855721668L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendRedirect(resp
				.encodeRedirectURL("/bq/close/professional-profile"));
	}

}
