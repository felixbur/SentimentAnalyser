package com.felix.sentimentanalyser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.felix.sentimentanalyser.util.Constants;
import com.felix.util.KeyValues;
import com.felix.util.StringUtil;
import com.felix.util.logging.Log4JLogger;

public class DoIt extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	// to be called e.g.
	// http://localhost:8080/TextParserServer/ParseText?text=punto%20ab%20baujahr%202005%20maximal%2050.000%20kilometer%20mindestens%20500%20euro&formatOutput=true
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		req.setCharacterEncoding(Constants.CHAR_ENC);
        resp.setContentType("text/html; charset="+Constants.CHAR_ENC);
		resp.setCharacterEncoding(Constants.CHAR_ENC);

		Log4JLogger logger = (Log4JLogger) getServletContext().getAttribute("logger");
		String question = req.getParameter("q");
		String targetPage = req.getParameter("targetPage");
		String userID = req.getParameter("userID");
		KeyValues config = (KeyValues) getServletContext().getAttribute(
				"config");
		long now = 0;
		String retString = "";
		long timeUsed=0;
			try {
				now = System.currentTimeMillis();
				// do something interesting
				timeUsed = System.currentTimeMillis() - now;
			} catch (Exception e) {
				e.printStackTrace();
				retString += e.getMessage();
			}
		if (StringUtil.isFilled(targetPage)) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					targetPage);
			req.setAttribute("result", retString);
			req.setAttribute("query", question);
			req.setAttribute("time", timeUsed);
			rd.forward(req, resp);
		} else {
			PrintWriter out = resp.getWriter();
			logger.info("user " + userID + ", answer: " + retString);
			out.println(retString);
		}
	}
}
