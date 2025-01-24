package com.digital.school.model;

import lombok.Data;

@Data
public class StudentDashboardStats {
    private double attendanceRate;
    private double averageGrade;
    private int pendingHomework;
    private int upcomingExams;
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
    
    
    
    
    
    
}
