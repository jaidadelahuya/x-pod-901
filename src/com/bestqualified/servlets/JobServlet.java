package com.bestqualified.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bestqualified.bean.JobInformation;
import com.bestqualified.controllers.GeneralController;
import com.bestqualified.entities.Company;
import com.bestqualified.entities.Job;
import com.bestqualified.util.EntityConverter;
import com.bestqualified.util.MemcacheProvider;
import com.bestqualified.util.StringConstants;
import com.bestqualified.util.Util;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class JobServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4899765101035322998L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		String webSafeKey = req.getParameter("job-key");
		Key key = KeyFactory.stringToKey(webSafeKey);
		Object o = MemcacheProvider.JOBS.get(key);
		Job job = null;
		if(o == null) {
			job = EntityConverter.entityToJob(GeneralController.findByKey(key));
			MemcacheProvider.JOBS.put(key, job);
		}else {
			job = (Job) o;
		}
		
		Company com = null;
		JobInformation ji = new JobInformation();
		if(job.getCompany() != null) {
			com = EntityConverter.entityToCompany(GeneralController.findByKey(job.getCompany()));
			ji.setCompanyName(com.getCompanyName());
			if(com.getLogo() == null) {
				ji.setPictureUrl(StringConstants.DEFAULT_COMPANY_LOGO);
			}else {
				ji.setPictureUrl(Util.getPictureUrl(com.getLogo()));
			}
			ji.setCompanyDesc(com.getDescription().getValue());
		} else {
			ji.setCompanyName("Confidential");
			ji.setPictureUrl(StringConstants.DEFAULT_COMPANY_LOGO);
		}
		
		
		ji.setPageUrl(resp.encodeURL("/bq/open/job?job-key="+webSafeKey));
		ji.setWebKey(webSafeKey);
		ji.setJobTitle(job.getJobTitle());
		
		ji.setDatePosted(Util.getPostedTime(job.getDatePosted()));
		ji.setJobDesc(job.getDescription().getValue());
		ji.setExperience(job.getExperience());
		ji.setCareerLevel(job.getCareerLevel());
		ji.setLocation(job.getLocation());
		ji.setJobType(job.getJobType());
		ji.setApplicationUrl(job.getApplicationUrl());
		ji.setApplicationWebsite(job.getEducationLevel());
		ji.setDeadline(new SimpleDateFormat("dd-MMM-YYYY").format(job.getClosingDate()));
		
		synchronized (session) {
			session.setAttribute("jobInformation", ji);
		}
		resp.sendRedirect(resp.encodeRedirectURL("/bq/open/job-information"));
	}

}