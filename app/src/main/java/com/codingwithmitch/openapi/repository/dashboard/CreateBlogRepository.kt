package com.codingwithmitch.openapi.repository.dashboard

import com.codingwithmitch.openapi.api.dashboard.ApiDashboardService
import com.codingwithmitch.openapi.persistance.BlogPostDao
import com.codingwithmitch.openapi.repository.JobManager
import com.codingwithmitch.openapi.session.SessionManager
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val apiDashboardService: ApiDashboardService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("CreateBlogRepository")