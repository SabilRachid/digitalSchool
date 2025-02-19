package com.digital.school.model;

import lombok.Data;

@Data
public class StudentDashboardStats {

    private double attendanceRate;
    private double averageGrade;
    private int pendingHomework;
    private int upcomingExams;
	private Integer rank;
	private long totalStudents;
	private Double successRate;

	public StudentDashboardStats() {
	}

	public StudentDashboardStats(double attendanceRate, double averageGrade, int pendingHomework, int upcomingExams, Integer rank, long totalStudents, Double successRate) {
		this.attendanceRate = attendanceRate;
		this.averageGrade = averageGrade;
		this.pendingHomework = pendingHomework;
		this.upcomingExams = upcomingExams;
		this.rank = rank;
		this.totalStudents = totalStudents;
		this.successRate = successRate;
	}
	public double getAttendanceRate() {
		return attendanceRate;
	}
	public void setAttendanceRate(double attendanceRate) {
		this.attendanceRate = attendanceRate;
	}
	public double getAverageGrade() {
		return averageGrade;
	}
	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}
	public int getPendingHomework() {
		return pendingHomework;
	}
	public void setPendingHomework(int pendingHomework) {
		this.pendingHomework = pendingHomework;
	}
	public int getUpcomingExams() {
		return upcomingExams;
	}
	public void setUpcomingExams(int upcomingExams) {
		this.upcomingExams = upcomingExams;
	}


	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getRank() {
		return rank;
	}

	public void setTotalStudents(long totalStudents) {
		this.totalStudents = totalStudents;
	}

	public long getTotalStudents() {
		return totalStudents;
	}

	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}

	public Double getSuccessRate() {
		return successRate;
	}
}
