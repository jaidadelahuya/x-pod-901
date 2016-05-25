package com.bestqualified.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bestqualified.bean.AssessmentQuestionBean;
import com.bestqualified.bean.CorrectionBean;
import com.bestqualified.bean.FacebookAccessTokenResponse;
import com.bestqualified.bean.InterestedJob;
import com.bestqualified.bean.ManageProjectBean;
import com.bestqualified.bean.MyJobs;
import com.bestqualified.bean.ProView;
import com.bestqualified.bean.ProfessionalDashboard;
import com.bestqualified.bean.ProfessionalProfileBean;
import com.bestqualified.bean.ProjectBean1;
import com.bestqualified.bean.ProjectView;
import com.bestqualified.bean.RecruiterDashboardBean;
import com.bestqualified.bean.SignUpBean;
import com.bestqualified.bean.SocialUser;
import com.bestqualified.bean.SocialUser.SocialNetwork;
import com.bestqualified.controllers.GeneralController;
import com.bestqualified.entities.AssessmentQuestion;
import com.bestqualified.entities.Award;
import com.bestqualified.entities.CandidateProfile;
import com.bestqualified.entities.Certification;
import com.bestqualified.entities.Company;
import com.bestqualified.entities.Education;
import com.bestqualified.entities.Job;
import com.bestqualified.entities.Project;
import com.bestqualified.entities.Recruiter;
import com.bestqualified.entities.User;
import com.bestqualified.entities.WorkExperience;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Facet;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONTokener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Util {

	public static final String SERVICE_ACCOUNT = "bestqualified.profiliant@gmail.com";

	public static boolean notNull(String... args) {
		if (args == null) {
			return false;
		}
		for (String s : args) {
			if (s == null || s.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static String generateRandomCode(int minVal, int maxVal) {
		Random ran = new Random();
		return new Integer(minVal + ran.nextInt(maxVal)).toString();
	}

	public static String toSHA512(String str) {

		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(str.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1

			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

		} catch (NoSuchAlgorithmException e) { // TODO Auto-generated catch
												// block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static boolean sendEmail(String from, String to, String title,
			String body) throws AddressException, MessagingException {

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(Util.SERVICE_ACCOUNT,
					"Best Qualified Admin", "UTF-8"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(title);
			msg.setText(body);
			msg.setContent(body, "text/html");
			Transport.send(msg);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		}
		return true;

	}

	public static User signUpBeanToUser(SignUpBean sub) {
		User u = new User(sub.getFirstName(), sub.getLastName());
		u.setEmail(sub.getEmail());
		u.setFirstName(sub.getFirstName());
		u.setJoinedDate(new Date());
		u.setPassword(sub.getPassword());
		u.setLastName(sub.getLastName());
		if (sub.getUserType() != null) {
			u.setUserType(sub.getUserType().name());
		}

		return u;
	}

	public static void sendConfirmationCodeEmail(String email, String body)
			throws AddressException, MessagingException {
		String title = "Best Qualified Confirmation Code";
		String to = email.toLowerCase();
		Util.sendEmail(Util.SERVICE_ACCOUNT, to, title, body);
		// Util.sendEmailNotification( to, title, body);
	}

	public static String getConfirmationCodeEmailBody(String code, String name) {
		if (name == null) {
			name = "";
		}
		return "<body><div style='width: 40%; margin: 0 auto'>"
				+ "<img alt='Best Qualified' src='http://best-qualified.appspot.com/images/bq-logo.png'/>"
				+ "</div><div><h4 style='padding-bottom: 3%;'>Hello "
				+ name
				+ ",</h4>"
				+ "<h3 style='color:#d9534f'>Best Qualified Verification Code</h3><p>Your new account is almost ready.</p>"
				+ "<p>Before you can login you have to verfiy your email by entering this code: <strong style='color:#d9534f'>"
				+ code
				+ "</strong></p><p>This code is valid for the next 24hrs.</p>"
				+ "<p>Regards,</p><p>Best Qualified Team</p></div></body>";
	}

	public static String getAccountCreatedMsg(String firstName) {

		return "<body><div style='width: 40%; margin: 0 auto'>"
				+ "<img alt='Best Qualified' src='http://best-qualified.appspot.com/images/bq-logo.png'/>"
				+ "</div><div><h4 style='padding-bottom: 3%;'>Hello "
				+ firstName
				+ ",</h4>"
				+ "<h3 style='color:#d9534f'>Best Qualified Account Created Successfully</h3><p>Your new account is aready.</p>"
				+ "<p>Regards,</p><p>Best Qualified Team</p></div></body>";
	}

	public static User mergeUsers(User u, User u1) {
		u.setBirthDate(u1.getBirthDate());
		u.setEmails(u1.getEmails());
		u.setFacebookID(u1.getFacebookID());
		u.setGender(u.getGender());
		u.setGoogleID(u1.getGoogleID());
		u.setJoinedDate(u1.getJoinedDate());
		u.setLinkedInID(u1.getLinkedInID());
		u.setPhone(u1.getPhone());
		u.setProfilePicture(u1.getProfilePicture());
		u.setTwitterID(u1.getTwitterID());
		u.setUserInfo(u1.getUserInfo());
		u.setUserType(u1.getUserType());
		u.setVerified(u1.isVerified());
		return u;
	}

	public static SignUpBean userToSignUpBean(User u) {
		SignUpBean sub = new SignUpBean();
		sub.setEmail(u.getEmail());
		sub.setFirstName(u.getFirstName());
		sub.setLastName(u.getLastName());
		sub.setPassword(u.getPassword());
		return sub;
	}

	public static List<InterestedJob> getJobs(Key careerLevel,
			List<Key> education) {
		List<InterestedJob> l = null;
		if (careerLevel != null & education != null) {

		} else {
			Iterator<Entity> it = GeneralController.findAll(
					Job.class.getSimpleName(), 3);
			List<Job> jobs = new ArrayList<>();
			while (it.hasNext()) {
				jobs.add(EntityConverter.entityToJob(it.next()));
			}
			l = Util.toInterestedJobs(jobs);

		}
		return l;
	}

	private static List<InterestedJob> toInterestedJobs(List<Job> jobs) {
		List<InterestedJob> l = new ArrayList<>();
		List<Key> cKeys = new ArrayList<>();
		for (Job jb : jobs) {
			if (jb.getCompany() != null) {
				cKeys.add(jb.getCompany());
			}

		}
		Map<Key, Entity> cEnts = GeneralController.findByKeys(cKeys);
		for (Job j : jobs) {
			InterestedJob ij = new InterestedJob();
			if (j.getCompany() == null) {
				ij.setCompanyName("Confidential");
				ij.setPictureUrl(StringConstants.DEFAULT_COMPANY_LOGO);
			} else {
				Company c = EntityConverter.entityToCompany(cEnts.get(j
						.getCompany()));
				ij.setCompanyName(c.getCompanyName());
				ij.setCompanyKey(KeyFactory.keyToString(c.getId()));
				if (c.getLogo() == null) {
					ij.setPictureUrl(StringConstants.DEFAULT_COMPANY_LOGO);
				} else {
					ij.setPictureUrl(Util.getPictureUrl(c.getLogo()));
				}
			}
			ij.setJobKey(KeyFactory.keyToString(j.getId()));
			ij.setJobTitle(j.getJobTitle());

			ij.setPostedTime(getPostedTime(j.getDatePosted()));
			l.add(ij);
		}
		return l;
	}

	public static String getPostedTime(Date datePosted) {
		Date today = new Date();
		long difference = today.getTime() - datePosted.getTime();
		difference = TimeUnit.MILLISECONDS.toSeconds(difference);
		if (difference < 60) {
			return difference + " seconds ago";
		} else {
			difference = TimeUnit.SECONDS.toMinutes(difference);
			if (difference < 60) {
				return difference + " minutes ago";
			} else {
				difference = TimeUnit.MINUTES.toHours(difference);
				if (difference < 24) {
					return difference + " hours ago";
				} else if (difference < 48) {
					return "yesterday";
				} else {
					difference = TimeUnit.HOURS.toDays(difference);
					if (difference < 7) {
						return difference + " days ago";
					} else {
						difference = Math.round(difference / 7);
						if (difference < 5) {
							return difference + " weeks ago";
						} else {
							difference = Math.round(difference / 30);
							if (difference < 13) {
								return difference + " years ago";
							} else {
								return Math.round(difference / 355)
										+ " years ago";
							}

						}
					}
				}
			}
		}
	}

	public static String getPictureUrl(BlobKey key) {
		if (key == null) {
			return "/images/unknown-user.jpg";
		} else {
			ServingUrlOptions suo = ServingUrlOptions.Builder.withBlobKey(key);
			ImagesService is = ImagesServiceFactory.getImagesService();
			return is.getServingUrl(suo);
		}
	}

	public static long calculateProfileStrength(User u, CandidateProfile cp) {
		long strength = 0;
		if (u.getFirstName() != null) {
			strength += 8;
		}
		if (u.getLastName() != null) {
			strength += 8;
		}
		if (u.getEmail() != null) {
			strength += 8;
		}
		if (u.getPhone() != null) {
			strength += 8;
		}
		if (cp.getCurrentState() != null) {
			strength += 8;
		}
		if (cp.getCv() != null) {
			strength += 8;
		}
		if (cp.getCurrentEmployer() != null) {
			strength += 6;
		}
		if (cp.getWorkExperience() != null && cp.getWorkExperience().size() > 1) {
			strength += 6;
		}
		if (cp.getProfileDescription() != null) {
			strength += 6;
		}
		if (cp.getEducation() != null && cp.getEducation().size() > 1) {
			strength += 6;
		}

		if (cp.getAwards() != null && cp.getAwards().size() > 1) {
			strength += 6;
		}

		if (cp.getCertifications() != null && cp.getCertifications().size() > 1) {
			strength += 6;
		}
		if (u.getTagline() != null) {
			strength += 2;
		}
		if (u.getProfilePicture() != null) {
			strength += 2;
		}
		if (u.getBirthDate() != null) {
			strength += 2;
		}
		if (cp.getNationality() != null) {
			strength += 2;
		}
		if (cp.getStateOfOrigin() != null) {
			strength += 2;
		}
		if (cp.getLga() != null) {
			strength += 2;
		}
		return strength;
	}

	public static ProfessionalDashboard initProfessionalDashboardBean(User u,
			CandidateProfile cp) {
		ProfessionalDashboard pd = new ProfessionalDashboard();
		// pd.setArticles(Util.getDashboardArticles());
		pd.setProfessionalLevel(u.getProfessionalLevel());
		pd.setRating(u.getRating());
		pd.setCurrentEmployer(cp.getCurrentEmployer());
		pd.setiJobs(Util.getJobs(cp.getCareerLevel(), cp.getEducation()));
		pd.setName(u.getFirstName() + " " + u.getLastName());
		pd.setNoOfConnections(String.valueOf((cp.getConnections() == null) ? 0
				: cp.getConnections().size()));
		pd.setNoOfProfileViewers(String
				.valueOf((cp.getProfileViewers() == null) ? 0 : cp
						.getProfileViewers().size()));
		if (u.getPictureUrl() == null) {
			pd.setPictureUrl((u.getProfilePicture() == null) ? StringConstants.UNKNOWN_USER
					: Util.getPictureUrl(u.getProfilePicture()));
		} else {
			pd.setPictureUrl(u.getPictureUrl());
		}
		pd.setProfileStrength(Util.calculateProfileStrength(u, cp));
		pd.setProfileLevel(Util.getprofileLevel(pd.getProfileStrength()));
		pd.setProfileColor(Util.getProfileColor(pd.getProfileStrength()));
		pd.setTagline(u.getTagline());
		return pd;
	}

	private static String getProfileColor(long profileStrength) {
		if (profileStrength < 50) {
			return "red";
		} else if (profileStrength < 70) {
			return "orange";
		} else if (profileStrength < 85) {
			return "olive";
		} else if (profileStrength < 96) {
			return "green";
		} else {
			return "blue";
		}
	}

	private static String getprofileLevel(long profileStrength) {
		if (profileStrength < 50) {
			return "Beginner";
		} else if (profileStrength < 70) {
			return "Intermediate";
		} else if (profileStrength < 85) {
			return "Intermediate";
		} else if (profileStrength < 96) {
			return "Intermediate";
		} else {
			return "Best Qualified Star";
		}
	}

	public static FacebookAccessTokenResponse toFacebookAccessTokenResponse(
			String respString) {
		respString = respString.replace("{", "").replace("}", "")
				.replace("\"", "");
		String[] str = respString.split(",");
		FacebookAccessTokenResponse fatr = new FacebookAccessTokenResponse();
		for (String s : str) {
			String[] ss = s.split(":");
			if (ss[0].equalsIgnoreCase("access_token")) {
				if (ss.length > 1) {
					fatr.setAccessToken(ss[1]);
				}

			} else if (ss[0].equalsIgnoreCase("expires_in")) {
				if (ss.length > 1) {
					fatr.setExpires(ss[1]);
				}

			} else if (ss[0].equalsIgnoreCase("token_type")) {
				if (ss.length > 1) {
					fatr.setTokenType(ss[1]);
				}

			}

		}
		return fatr;
	}

	public static SocialUser toFaceBookSocialUser(String respString) {
		SocialUser su = new SocialUser();
		su.setNetwork(SocialNetwork.FACEBOOK);
		JSONTokener jt = new JSONTokener(respString);
		JSONObject jo;
		try {
			jo = new JSONObject(jt);
			if (respString.contains("email")) {
				su.setEmail(jo.getString("email"));
			}
			if (respString.contains("first_name")) {
				su.setFirstName(jo.getString("first_name"));
			}
			if (respString.contains("gender")) {
				su.setGender(jo.getString("gender"));
			}
			if (respString.contains("id")) {
				su.setId(jo.getString("id"));
			}
			if (respString.contains("last_name")) {
				su.setLastName(jo.getString("last_name"));
			}
			su.setPictureUrl(jo.getJSONObject("picture").getJSONObject("data")
					.getString("url"));
			su.setVerified(jo.getBoolean("verified"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return su;
	}

	public static void logUserIn(User u, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		u.setAuthenticated(true);
		Entity e = GeneralController.findByKey(u.getUserInfo());
		CandidateProfile cp = null;
		if (u.getUserType().equalsIgnoreCase(User.UserType.PROFESSIONAL.name())) {
			cp = EntityConverter.entityToCandidateProfile(e, u.getUserKey());
			synchronized (session) {
				session.setAttribute("professionalProfile", cp);
			}
		} else if (u.getUserType().equalsIgnoreCase(
				User.UserType.RECRUITER.name())) {
			Recruiter r = EntityConverter.entityToRecruiter(e);
		}

		RequestDispatcher rd = req
				.getRequestDispatcher("/bq/closed/init-dashboard");
		rd.forward(req, resp);

	}

	public static User socialUserToUser(SocialUser su) {
		User u = new User(su.getFirstName(), su.getLastName());
		u.setGender(su.getGender());
		u.setTagline(su.getHeadline());
		u.setPictureUrl(su.getPictureUrl());
		u.setJoinedDate(new Date());
		if (!u.getEmails().contains(su.getEmail())) {
			u.getEmails().add(su.getEmail());
		}
		switch (su.getNetwork()) {
		case FACEBOOK:
			u.setFacebookID(su.getId());
			break;
		case LINKEDIN:
			u.setLinkedInID(su.getId());
			break;
		case TWITTER:
			u.setTwitterID(su.getId());
			break;
		case GOOGLE:
			u.setGoogleID(su.getId());
		}
		return u;
	}

	public static MyJobs getMyJobs(Map<String, Object> map, CandidateProfile cp) {
		MyJobs mjs = new MyJobs();
		if (cp.getJobsApplied() == null) {
			mjs.setApplications(0);
		} else {
			mjs.setApplications(cp.getJobsApplied().size());
		}

		if (cp.getJobAlerts() == null) {
			mjs.setJobAlerts(0);
		} else {
			mjs.setJobAlerts(cp.getJobAlerts().size());
		}

		mjs.setJobsCursor((String) map.get("cursor"));
		if (cp.getSavedJobs() == null) {
			mjs.setSavedJobs(0);
		} else {
			mjs.setSavedJobs(cp.getSavedJobs().size());
		}

		List<Job> jobs = new ArrayList<>();
		QueryResultList<Entity> qrl = (QueryResultList<Entity>) map
				.get("entities");
		for (Entity e : qrl) {
			jobs.add(EntityConverter.entityToJob(e));
		}
		mjs.setiJobs(Util.toInterestedJobs(jobs));
		return mjs;
	}

	public static MyJobs updateMyJobs(MyJobs mjs, Map<String, Object> map,
			CandidateProfile cp) {
		if (cp.getJobsApplied() == null) {
			mjs.setApplications(0);
		} else {
			mjs.setApplications(cp.getJobsApplied().size());
		}

		if (cp.getJobAlerts() == null) {
			mjs.setJobAlerts(0);
		} else {
			mjs.setJobAlerts(cp.getJobAlerts().size());
		}

		mjs.setJobsCursor((String) map.get("cursor"));
		if (cp.getSavedJobs() == null) {
			mjs.setSavedJobs(0);
		} else {
			mjs.setSavedJobs(cp.getSavedJobs().size());
		}
		List<Job> jobs = new ArrayList<>();
		QueryResultList<Entity> qrl = (QueryResultList<Entity>) map
				.get("entities");
		for (Entity e : qrl) {
			jobs.add(EntityConverter.entityToJob(e));
		}
		mjs.setiJobs(Util.toInterestedJobs(jobs));
		return mjs;

	}
	
	public static void addToSearchIndex(Job job, Company c) {
		Document.Builder db = Document.newBuilder();
		db = db.setId(KeyFactory.keyToString(job.getId()))
				.addField(Field.newBuilder().setName("jobTitle").setAtom(job.getJobTitle()))
				.addField(Field.newBuilder().setName("jobTitle").setText(job.getJobTitle()))
				.addField(Field.newBuilder().setName("location").setAtom(job.getLocation()))
				.addField(Field.newBuilder().setName("salaryRange").setAtom(job.getSalaryRange()))
				.addField(Field.newBuilder().setName("careerLevel").setAtom(job.getCareerLevel()))
				.addField(Field.newBuilder().setName("jobType").setAtom(job.getJobType()))
				.addField(Field.newBuilder().setName("experience").setAtom(job.getExperience()))
				.addField(Field.newBuilder().setName("jobCategory").setAtom(job.getJobCategory()))
				.addField(Field.newBuilder().setName("datePosted").setDate(job.getDatePosted())).
				addField(Field.newBuilder().setName("companyName").setText(c.getCompanyName()))
				;
		
		SearchDocumentIndexService.indexDocument("jobs",
				db.build());
	}

	/*public static void addToSearchIndex(Job job, Company c) {
		Document.Builder db = Document.newBuilder();
		db = db.setId(KeyFactory.keyToString(job.getId()));
		db = db.addFacet(Facet.withAtom("location", job.getLocation()));
		db = db.addFacet(Facet.withAtom("educationLevel",
				job.getEducationLevel()));
		db = db.addFacet(Facet.withAtom("salaryRange", job.getSalaryRange()));
		db = db.addFacet(Facet.withAtom("careerLevel", job.getCareerLevel()));
		db = db.addFacet(Facet.withAtom("jobType", job.getJobType()));
		db = db.addFacet(Facet.withAtom("experience", job.getExperience()));
		db = db.addFacet(Facet.withAtom("companyName", c.getCompanyName()));
		db = db.addFacet(Facet.withAtom("jobTitle", job.getJobTitle()));
		db = db.addFacet(Facet.withAtom("jobCategory", job.getJobCategory()));
		db = db.addField(Field.newBuilder().setName("location")
				.setText(job.getLocation()));
		db = db.addField(Field.newBuilder().setName("description")
				.setHTML(job.getDescription().getValue()));
		db = db.addField(Field.newBuilder().setName("companyName")
				.setText(c.getCompanyName()));
		db = db.addField(Field.newBuilder().setName("jobTitle")
				.setText(job.getJobTitle()));
		db = db.addField(Field.newBuilder().setName("companyKey")
				.setText(KeyFactory.keyToString(c.getId())));
		db = db.addField(Field.newBuilder().setName("datePosted")
				.setDate(job.getDatePosted()));

		SearchDocumentIndexService.indexDocument("JobSearchDocuments",
				db.build());

	}*/

	public static ProfessionalProfileBean createProfessionalProfileBean(User u,
			CandidateProfile cp) {
		ProfessionalProfileBean ppb = new ProfessionalProfileBean();
		// award
		List<Award> awards = new ArrayList<>();
		if (cp.getAwards() != null) {
			for (Key k : cp.getAwards()) {
				awards.add(EntityConverter.entityToAward(GeneralController
						.findByKey(k)));
			}
		} else {
			cp.setAwards(new ArrayList<Key>());
		}
		ppb.setAwards(awards);
		// certifications
		List<Certification> certifications = new ArrayList<>();
		if (cp.getCertifications() != null) {

			for (Key k : cp.getCertifications()) {
				certifications.add(EntityConverter
						.entityToCertification(GeneralController.findByKey(k)));
			}
		} else {
			cp.setCertifications(new ArrayList<Key>());
		}
		ppb.setCertifications(certifications);

		// education
		List<Education> education = new ArrayList<>();
		if (cp.getEducation() != null) {
			for (Key k : cp.getEducation()) {
				education.add(EntityConverter
						.entityToEducation(GeneralController.findByKey(k)));
			}
		} else {
			cp.setEducation(new ArrayList<Key>());
		}

		// work experience
		List<WorkExperience> workExperience = new ArrayList<>();
		if (cp.getWorkExperience() != null) {
			for (Key k : cp.getWorkExperience()) {
				workExperience
						.add(EntityConverter
								.entityToWorkExperience(GeneralController
										.findByKey(k)));
			}
		} else {
			cp.setWorkExperience(new ArrayList<Key>());
		}

		if (cp.getCv() != null) {
			ppb.setCvSafeString(cp.getCv().getKeyString());
		}
		ppb.setCv(Util.getBlobFileName(cp.getCv()));

		ppb.setWorkExperience(workExperience);
		ppb.setEducation(education);
		ppb.setCurrentEmployer(cp.getCurrentEmployer());
		ppb.setEmail(u.getEmail());
		ppb.setFirstName(u.getFirstName());
		ppb.setLastName(u.getLastName());
		ppb.setPhone(u.getPhone());
		if (cp.getProfileDescription() != null) {
			ppb.setProfileSummary(cp.getProfileDescription().getValue());
		}
		ppb.setTagline(u.getTagline());
		return ppb;
	}

	private static String getBlobFileName(BlobKey blobKey) {
		if (blobKey == null) {
			return "";
		}
		BlobInfoFactory bif = new BlobInfoFactory();
		BlobInfo bi = bif.loadBlobInfo(blobKey);
		return bi.getFilename();
	}

	public static RecruiterDashboardBean initRecruiterDashboardBean(User u,
			Recruiter r) {
		RecruiterDashboardBean rdb = new RecruiterDashboardBean();
		rdb.setFirstName(u.getFirstName());
		rdb.setLastName(u.getLastName());
		rdb.setTagline(u.getTagline());
		if (r.getCompany() != null) {
			Company c = EntityConverter.entityToCompany(GeneralController
					.findByKey(r.getCompany()));
			rdb.setCompany(c.getCompanyName());
		}
		if (r.getConnections() != null) {
			rdb.setNoConnections(String.valueOf(r.getConnections().size()));
		} else {
			rdb.setNoConnections(String.valueOf(0));
		}
		if (r.getProjects() == null) {
			rdb.setNumberOfProjects(0);
		} else {
			rdb.setNumberOfProjects(r.getProjects().size());
		}

		// rdb.setProjectView(Util.getProjectViews(r.getProjects()));
		Map<String, Set<Key>> map = Util.getApplicants(r.getProjects());
		List<Key> applicants = Util.getApplicantsList(map);
		List<ProView> users = new ArrayList<>();
		for (Key k : applicants) {
			ProView pv = Util.getProview(map, k);
			if (pv != null) {
				users.add(pv);
			}
		}
		rdb.setApplicants(users);
		return rdb;
	}

	private static ProView getProview(Map<String, Set<Key>> map, Key userKey) {
		User u = EntityConverter.entityToUser(GeneralController
				.findByKey(userKey));
		ProView p = new ProView();
		if (u.getUserType().equalsIgnoreCase(User.UserType.PROFESSIONAL.name())) {
			CandidateProfile cp = EntityConverter.entityToCandidateProfile(
					GeneralController.findByKey(u.getUserInfo()), userKey);
			for (String s : map.keySet()) {
				Set<Key> kys = map.get(s);
				for (Key k : kys) {
					if (k == userKey) {
						p.setProjectName(s);
						break;
					}
				}
				break;
			}
			p.setCountry(cp.getCurrentCountry());
			p.setFirstName(u.getFirstName());
			p.setLastName(u.getLastName());
			if (cp.getConnections() != null) {
				p.setNumberOfConnections(String.valueOf(cp.getConnections()
						.size()));
			}
			p.setState(cp.getCurrentState());

			// education
			if (cp.getEducation() != null) {
				Education edu = null;
				Map<Key, Entity> mp = GeneralController.findByKeys(cp
						.getEducation());
				for (Key k : cp.getEducation()) {
					Education e = EntityConverter.entityToEducation(mp.get(k));
					if (edu == null) {
						edu = e;
					} else if (e.getEndYear().equalsIgnoreCase("current")
							|| e.getEndYear().equalsIgnoreCase("to date")) {
						edu = e;
						break;
					} else if (Integer.parseInt(e.getEndYear()) > Integer
							.parseInt(edu.getEndYear())) {
						edu = e;
					} else if (Integer.parseInt(e.getEndYear()) == Integer
							.parseInt(edu.getEndYear())) {
						if (Integer.parseInt(e.getEndMonth()) > Integer
								.parseInt(edu.getEndMonth())) {
							edu = e;
						}
					}

				}
				p.setEducation(edu);
			}

			// work experience
			if (cp.getWorkExperience() != null) {
				WorkExperience we = null;
				Map<Key, Entity> mp = GeneralController.findByKeys(cp
						.getWorkExperience());
				for (Key k : cp.getWorkExperience()) {
					WorkExperience wex = EntityConverter
							.entityToWorkExperience(mp.get(k));
					if (we == null) {
						we = wex;
					} else if (wex.getEndYear().equalsIgnoreCase("current")
							|| wex.getEndYear().equalsIgnoreCase("to date")) {
						we = wex;
						break;
					} else if (Integer.parseInt(wex.getEndYear()) > Integer
							.parseInt(we.getEndYear())) {
						we = wex;
					} else if (Integer.parseInt(wex.getEndYear()) == Integer
							.parseInt(we.getEndYear())) {
						if (Integer.parseInt(wex.getEndMonth()) > Integer
								.parseInt(we.getEndMonth())) {
							we = wex;
						}
					}

				}
				p.setWorkExperience(we);
			}

		}
		return p;
	}

	private static List<Key> getApplicantsList(Map<String, Set<Key>> map) {
		List<Key> applicants = new ArrayList<>();
		Set<String> s = map.keySet();
		for (String st : s) {
			applicants.addAll(map.get(st));
		}
		return applicants;
	}

	private static Map<String, Set<Key>> getApplicants(List<Key> projects) {
		Map<String, Set<Key>> mp = new HashMap<String, Set<Key>>();
		if (projects == null) {
			return mp;
		} else {
			Map<Key, Entity> ents = GeneralController.findByKeys(projects);
			for (Key k : projects) {
				Set<Key> set = new HashSet<>();
				Project p = EntityConverter.entityToProject(ents.get(k));
				if (p.getJobs() != null) {
					Job j = EntityConverter.entityToJob(GeneralController
							.findByKey(p.getJobs()));
					List<Key> applicants = j.getNewApplicants();
					if (applicants != null) {
						set.addAll(applicants);
					}

					mp.put(p.getName(), set);
				}
			}
			return mp;
		}

	}

	private static List<ProjectView> getProjectViews(List<Key> projects) {
		if (projects != null) {
			List<ProjectView> ps = new ArrayList<>();
			Map<Key, Entity> ents = GeneralController.findByKeys(projects);
			for (Key k : projects) {
				ps.add(Util.projectToView(EntityConverter.entityToProject(ents
						.get(k))));
			}
			return ps;

		} else {
			return new ArrayList<ProjectView>();
		}
	}

	private static ProjectView projectToView(Project project) {
		ProjectView pv = new ProjectView();
		pv.setName(project.getName());
		Job j = EntityConverter.entityToJob(GeneralController.findByKey(project
				.getId()));
		pv.setView(j.getViewers().size());
		pv.setWebKey(project.getSafeKey());
		return pv;
	}

	public static void sendApplicationEmails(User u, CandidateProfile cp,
			Job job, String applicationEmail, HttpServletRequest req)
			throws IOException {
		String partUrl = req.getScheme() + "://" + req.getServerName() + ":"
				+ req.getServerPort();
		try {
			sendRecruiterEmail(u, cp, job, applicationEmail, partUrl);
			sendApplicantsEmail(u, job);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void sendRecruiterEmail(User u, CandidateProfile cp,
			Job job, String applicationEmail, String partUrl)
			throws IOException {
		String cvPath = partUrl + "/bq/open/get-cv?safe-key="
				+ cp.getCv().getKeyString();
		String body = "<body><div style='width: 40%; margin: 0 auto'>"
				+ "<img alt='Best Qualified' src='http://best-qualified.appspot.com/images/bq-logo.png'/>"
				+ "</div><div><h4 style='padding-bottom: 3%;'>Hello,</h4>"
				+ "<p>" + u.getFirstName() + " " + u.getLastName()
				+ "has shown interest in your job post titled "
				+ job.getJobTitle() + "has been sent.</p>"
				+ "<p>Download the attached CV <a target='_blank' href='"
				+ cvPath + "'>here</a>.</p>"
				+ "<p>Regards,</p><p>Best Qualified Admin</p></div></body>";
		try {
			sendEmail(Util.SERVICE_ACCOUNT, u.getEmail(), "Application for "
					+ job.getJobTitle(), body);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void sendRecruiterEmail1(User u, CandidateProfile cp,
			Job job, String applicationEmail, String partUrl)
			throws IOException {
		String body = "<body><div style='width: 40%; margin: 0 auto'>"
				+ "<img alt='Best Qualified' src='http://best-qualified.appspot.com/images/bq-logo.png'/>"
				+ "</div><div><h4 style='padding-bottom: 3%;'>Hello,</h4>"
				+ "<p>" + u.getFirstName() + " " + u.getLastName()
				+ "has shown interest in your job post titled "
				+ job.getJobTitle() + "has been sent.</p>"
				+ "<p>Kindly find attached the submited CV.</p>"
				+ "<p>Regards,</p><p>Best Qualified Admin</p></div></body>";
		URL url = new URL(partUrl + "/bq/close/serve-blob");
		HTTPResponse resp = URLFetchServiceFactory.getURLFetchService().fetch(
				url);
		byte[] content = resp.getContent();
		String fileName = u.getFirstName() + " " + u.getLastName();
		sendMailWithAttachment(Util.SERVICE_ACCOUNT, applicationEmail,
				"Application for " + job.getJobTitle(), body, content, fileName);
	}

	private static void sendMailWithAttachment(String from, String to,
			String title, String body, byte[] attachmentData, String fileName) {

		Multipart mp = new MimeMultipart();
		MimeBodyPart htmlPart = new MimeBodyPart();
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			htmlPart.setContent(body, "text/html");
			mp.addBodyPart(htmlPart);
			MimeBodyPart attachment = new MimeBodyPart();
			InputStream attachmentDataStream = new ByteArrayInputStream(
					attachmentData);
			attachment.setFileName(fileName);
			attachment.setContent(attachmentDataStream, "application/msword");
			mp.addBodyPart(attachment);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(Util.SERVICE_ACCOUNT,
					"Best Qualified Admin", "UTF-8"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(title);
			msg.setContent(mp);
			Transport.send(msg);
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		}

	}

	private static void sendApplicantsEmail(User u, Job job)
			throws AddressException, MessagingException {
		String body = "<body><div style='width: 40%; margin: 0 auto'>"
				+ "<img alt='Best Qualified' src='http://best-qualified.appspot.com/images/bq-logo.png'/>"
				+ "</div><div><h4 style='padding-bottom: 3%;'>Hello "
				+ u.getFirstName() + ",</h4>"
				+ "<p>Your application for the job titled " + job.getJobTitle()
				+ "has been sent.</p>"
				+ "<p>You will be contacted by the recruiter shortly.</p>"
				+ "<p>Regards,</p><p>Best Qualified Admin</p></div></body>";
		sendEmail(Util.SERVICE_ACCOUNT, u.getEmail(),
				"Your Applicatio has been sent", body);

	}

	public static List<ProjectBean1> toProjectBean1(List<Project> l1) {
		List<ProjectBean1> pb1 = new ArrayList<>();
		for (Project p : l1) {
			ProjectBean1 pb = new ProjectBean1();
			pb.setDateCreated(new SimpleDateFormat("dd-MMM-yyyy").format(p
					.getDateCreated()));
			pb.setDescription(p.getDescription().getValue());
			pb.setName(p.getName());
			if (p.getProfiles() == null) {
				pb.setSavedProfile(0);
			} else {
				pb.setSavedProfile(p.getProfiles().size());
			}
			if (p.getSavedSearch() == null) {
				pb.setSavedSearch(0);
				;
			} else {
				pb.setSavedSearch(p.getSavedSearch().size());
			}

			pb.setWebKey(p.getSafeKey());
			if (p.getJobs() != null) {
				Job j = Util.getJobFromCache(p.getJobs());
				if (j.getInvitesSent() == null) {
					pb.setInviteSent(0);
				} else {
					pb.setInviteSent(j.getInvitesSent().size());
				}
				if (j.getNewApplicants() == null) {
					pb.setNewApplicants(0);
					pb.setTotalApplicants(0);
				} else {
					pb.setNewApplicants(j.getNewApplicants().size());
					if (j.getApplicants() == null) {
						pb.setTotalApplicants(j.getNewApplicants().size() + 0);
					} else {
						pb.setTotalApplicants(j.getNewApplicants().size()
								+ j.getApplicants().size());
					}
				}
			}
			pb1.add(pb);
		}
		return pb1;
	}

	public static Job getJobFromCache(Key key) {
		Object o = MemcacheProvider.JOBS.get(key);
		Job job = null;
		if (o == null) {
			job = EntityConverter.entityToJob(GeneralController.findByKey(key));
			MemcacheProvider.JOBS.put(key, job);
		} else {
			job = (Job) o;
		}
		return job;
	}

	public static ManageProjectBean getManageProjectBean(List<ProjectBean1> l2) {
		ManageProjectBean mpb = new ManageProjectBean();
		for (ProjectBean1 pb1 : l2) {
			mpb.setNewApplicants(mpb.getNewApplicants()
					+ pb1.getNewApplicants());
			mpb.setSavedProfiles(mpb.getSavedProfiles() + pb1.getSavedProfile());
			mpb.setTotalApplicants(mpb.getTotalApplicants()
					+ pb1.getTotalApplicants());
			mpb.setSavedSearch(mpb.getSavedSearch() + pb1.getSavedSearch());
		}
		mpb.setPb1(l2);
		return mpb;
	}

	public static List<AssessmentQuestion> getAssessmentQuestions(String level) {
		List<AssessmentQuestion> qs = new ArrayList<>();

		AssessmentQuestion a1 = new AssessmentQuestion();
		List<EmbeddedEntity> alts = new ArrayList<>();
		EmbeddedEntity ee1 = new EmbeddedEntity();
		ee1.setUnindexedProperty("correct", true);
		ee1.setUnindexedProperty("text", "Alhaji, Muhammed Buhari");
		EmbeddedEntity ee2 = new EmbeddedEntity();
		ee2.setUnindexedProperty("correct", false);
		ee2.setUnindexedProperty("text", "Gen. Sani Abacha");
		EmbeddedEntity ee3 = new EmbeddedEntity();
		ee3.setUnindexedProperty("correct", false);
		ee3.setUnindexedProperty("text", "Gen. Olusegun Obasanjo");
		EmbeddedEntity ee4 = new EmbeddedEntity();
		ee4.setUnindexedProperty("correct", false);
		ee4.setUnindexedProperty("text", "Alhaji Sheu Yar�dua");
		EmbeddedEntity ee5 = new EmbeddedEntity();
		ee5.setUnindexedProperty("correct", false);
		ee5.setUnindexedProperty("text", "Bola Ahmed Tinibu (Jagaban)");
		alts.add(ee5);
		alts.add(ee4);
		alts.add(ee3);
		alts.add(ee2);
		alts.add(ee1);
		a1.setAlternatives(alts);
		a1.setBody(new Text("Who is the Current President of Nigeria?"));
		a1.setCategory(level);
		qs.add(a1);

		AssessmentQuestion a2 = new AssessmentQuestion();
		List<EmbeddedEntity> alts2 = new ArrayList<>();
		EmbeddedEntity ee11 = new EmbeddedEntity();
		ee11.setUnindexedProperty("correct", false);
		ee11.setUnindexedProperty("text", "Alhaji Tafawa Balewa");
		EmbeddedEntity ee21 = new EmbeddedEntity();
		ee21.setUnindexedProperty("correct", false);
		ee21.setUnindexedProperty("text", "Goodluck Johnathan");
		EmbeddedEntity ee31 = new EmbeddedEntity();
		ee31.setUnindexedProperty("correct", false);
		ee31.setUnindexedProperty("text", "Gen. Ibrahim Babangida");
		EmbeddedEntity ee41 = new EmbeddedEntity();
		ee41.setUnindexedProperty("correct", true);
		ee41.setUnindexedProperty("text", "Dr. Nnamdi Azikwe");
		EmbeddedEntity ee51 = new EmbeddedEntity();
		ee51.setUnindexedProperty("correct", false);
		ee51.setUnindexedProperty("text", "Bola Ahmed Tinubu (Jagaban)");
		alts2.add(ee51);
		alts2.add(ee41);
		alts2.add(ee31);
		alts2.add(ee21);
		alts2.add(ee11);
		a2.setAlternatives(alts2);
		a2.setBody(new Text("Who was the first president of Nigeria?"));
		a2.setCategory(level);
		qs.add(a2);

		AssessmentQuestion a3 = new AssessmentQuestion();
		List<EmbeddedEntity> alts3 = new ArrayList<>();
		EmbeddedEntity ee12 = new EmbeddedEntity();
		ee12.setUnindexedProperty("correct", false);
		ee12.setUnindexedProperty("text", "Moshood Abiola");
		EmbeddedEntity ee22 = new EmbeddedEntity();
		ee22.setUnindexedProperty("correct", true);
		ee22.setUnindexedProperty("text", "Gen. Ibrahim Gbadamosi Babangida");
		EmbeddedEntity ee32 = new EmbeddedEntity();
		ee32.setUnindexedProperty("correct", false);
		ee32.setUnindexedProperty("text", "Mr. Fela Anikulapo Kuti");
		EmbeddedEntity ee42 = new EmbeddedEntity();
		ee42.setUnindexedProperty("correct", false);
		ee42.setUnindexedProperty("text", "Gen. Olusegun Obasanjo");
		EmbeddedEntity ee52 = new EmbeddedEntity();
		ee52.setUnindexedProperty("correct", false);
		ee52.setUnindexedProperty("text", "Bola Ahmed Tinubu (Jagaban)");
		alts3.add(ee52);
		alts3.add(ee42);
		alts3.add(ee32);
		alts3.add(ee22);
		alts3.add(ee12);
		a3.setAlternatives(alts3);
		a3.setBody(new Text(
				"Who moved the Federal Capital of Nigeria from Lagos to Abuja?"));
		a3.setCategory(level);
		qs.add(a3);

		return qs;
		/*
		 * List<Entity> ents =
		 * GeneralController.getAssessmentQuestionsBylevel(level);
		 * List<AssessmentQuestion> qs = new ArrayList<>(); for(Entity e : ents)
		 * { qs.add(EntityConverter.entityToAssessmentQuestion(e)); } return qs;
		 */
	}

	public static Map<String, Object> toAssessmentQuestionBean(
	// ninth commit
			List<AssessmentQuestion> qs) {
		Map<String, Object> m = new HashMap<>();
		List<AssessmentQuestionBean> l = new ArrayList<>();
		List<CorrectionBean> l1 = new ArrayList<>();
		for (AssessmentQuestion aq : qs) {
			CorrectionBean cb = new CorrectionBean();
			AssessmentQuestionBean aqb = new AssessmentQuestionBean();
			aqb.setQuestion(aq.getBody().getValue());
			aqb.setWebkey(aq.getWebKey());
			cb.setWebkey(aq.getWebKey());
			// cb.setExplanation(aq.getExplanation().getValue());
			List<EmbeddedEntity> ees = aq.getAlternatives();
			Map<String, String> map = new HashMap<>();
			int i = 0;
			for (EmbeddedEntity ee : ees) {
				map.put(String.valueOf(i), (String) ee.getProperty("text"));

				Boolean b = (Boolean) ee.getProperty("correct");
				if (b) {
					cb.setAnswer(String.valueOf(i));
				}

				i++;

			}
			aqb.setCorrectAnswer("");
			aqb.setChoices(map);
			l.add(aqb);
			l1.add(cb);

		}
		m.put("q", l);
		m.put("c", l1);
		return m;
	}

	public static List<CorrectionBean> toCorrectionBean(String answers) {
		JsonParser jsonParser = new JsonParser();
      
        JsonArray jsonArr = (JsonArray)jsonParser.parse(answers);;
        Gson googleJson = new Gson();
        Type listType = new TypeToken<List<AssessmentQuestionBean>>() {}.getType();
        ArrayList<AssessmentQuestionBean> jsonObjList = googleJson.fromJson(jsonArr, listType);
        return assessmentQuestionToCorrectionBean(jsonObjList);
	}

	private static List<CorrectionBean> assessmentQuestionToCorrectionBean(
			ArrayList<AssessmentQuestionBean> jsonObjList) {
		List<CorrectionBean> cbs = new ArrayList<>();
		for(AssessmentQuestionBean aqb : jsonObjList) {
			CorrectionBean cb = new CorrectionBean();
			cb.setWebkey(aqb.getWebkey());
			cb.setAnswer(aqb.getCorrectAnswer());
			cbs.add(cb);
		}
		return cbs;
	}

	public static int markAssessment(List<CorrectionBean> cb,
			List<CorrectionBean> userAns) {
		int score = 0;
		for (CorrectionBean ctb : cb) {
			String key = ctb.getWebkey();
			for (CorrectionBean ctb2 : userAns) {
				if (ctb2.getWebkey().equalsIgnoreCase(key)
						&& ctb2.getAnswer().equalsIgnoreCase(ctb.getAnswer())) {
					score++;
					break;
				}
			}
		}
		return score;
	}

	public static double getRating(int score, int total) {
		return Math.round(score*5/total);
	}
}
