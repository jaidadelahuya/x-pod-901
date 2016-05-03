package com.bestqualified.bean;

import java.io.Serializable;
import java.util.List;

public class ProfessionalDashboard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 516639664385901621L;
	private String name, pictureUrl, tagline, currentEmployer, noOfConnections,
			noOfProfileViewers, profileLevel, profileColor;
	private long profileStrength;
	private List<InterestedJob> iJobs;
	private List<Article> articles;

	public String getProfileColor() {
		return profileColor;
	}

	public void setProfileColor(String profileColor) {
		this.profileColor = profileColor;
	}

	public String getProfileLevel() {
		return profileLevel;
	}

	public void setProfileLevel(String profileLevel) {
		this.profileLevel = profileLevel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getCurrentEmployer() {
		return currentEmployer;
	}

	public void setCurrentEmployer(String currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public String getNoOfConnections() {
		return noOfConnections;
	}

	public void setNoOfConnections(String noOfConnections) {
		this.noOfConnections = noOfConnections;
	}

	public String getNoOfProfileViewers() {
		return noOfProfileViewers;
	}

	public void setNoOfProfileViewers(String noOfProfileViewers) {
		this.noOfProfileViewers = noOfProfileViewers;
	}

	public long getProfileStrength() {
		return profileStrength;
	}

	public void setProfileStrength(long profileStrength) {
		this.profileStrength = profileStrength;
	}

	public List<InterestedJob> getiJobs() {
		return iJobs;
	}

	public void setiJobs(List<InterestedJob> iJobs) {
		this.iJobs = iJobs;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	@Override
	public String toString() {
		return "ProfessionalDashboard [name=" + name + ", pictureUrl="
				+ pictureUrl + ", tagline=" + tagline + ", currentEmployer="
				+ currentEmployer + ", noOfConnections=" + noOfConnections
				+ ", noOfProfileViewers=" + noOfProfileViewers
				+ ", profileStrength=" + profileStrength + ", iJobs=" + iJobs
				+ ", articles=" + articles + "]";
	}

}