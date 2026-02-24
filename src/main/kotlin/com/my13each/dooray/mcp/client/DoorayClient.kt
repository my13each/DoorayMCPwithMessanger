package com.my13each.dooray.mcp.client

import com.my13each.dooray.mcp.types.*

interface DoorayClient {

    /** 접근 가능한 위키 목록을 조회합니다. */
    suspend fun getWikis(page: Int? = null, size: Int? = null): WikiListResponse

    /** 특정 프로젝트의 위키 목록을 조회합니다. */
    suspend fun getWikiPages(projectId: String): WikiPagesResponse

    /** 특정 상위 페이지의 자식 위키 페이지들을 조회합니다. */
    suspend fun getWikiPages(projectId: String, parentPageId: String): WikiPagesResponse

    /** 특정 위키 페이지의 상세 정보를 조회합니다. */
    suspend fun getWikiPage(projectId: String, pageId: String): WikiPageResponse

    /** 새로운 위키 페이지를 생성합니다. */
    suspend fun createWikiPage(
        wikiId: String,
        request: CreateWikiPageRequest
    ): CreateWikiPageResponse

    /** 위키 페이지를 수정합니다. */
    suspend fun updateWikiPage(
        wikiId: String,
        pageId: String,
        request: UpdateWikiPageRequest
    ): DoorayApiUnitResponse

    // ============ 프로젝트 업무 관련 API ============

    /** 프로젝트 내에 업무를 생성합니다. */
    suspend fun createPost(projectId: String, request: CreatePostRequest): CreatePostApiResponse

    /** 업무 목록을 조회합니다. */
    suspend fun getPosts(
        projectId: String,
        page: Int? = null,
        size: Int? = null,
        fromMemberIds: List<String>? = null,
        fromEmailAddress: String? = null,
        toMemberIds: List<String>? = null,
        toMemberSize: Int? = null,
        ccMemberIds: List<String>? = null,
        tagIds: List<String>? = null,
        parentPostId: String? = null,
        postNumber: String? = null,
        postWorkflowClasses: List<String>? = null,
        postWorkflowIds: List<String>? = null,
        milestoneIds: List<String>? = null,
        subjects: String? = null,
        createdAt: String? = null,
        updatedAt: String? = null,
        dueAt: String? = null,
        order: String? = null
    ): PostListResponse

    /** 업무 상세 정보를 조회합니다. */
    suspend fun getPost(projectId: String, postId: String): PostDetailResponse

    /** 업무를 수정합니다. */
    suspend fun updatePost(
        projectId: String,
        postId: String,
        request: UpdatePostRequest
    ): UpdatePostResponse

    /** 특정 담당자의 상태를 변경합니다. */
    suspend fun updatePostUserWorkflow(
        projectId: String,
        postId: String,
        organizationMemberId: String,
        workflowId: String
    ): DoorayApiUnitResponse

    /** 업무 전체의 상태를 변경합니다. */
    suspend fun setPostWorkflow(
        projectId: String,
        postId: String,
        workflowId: String
    ): DoorayApiUnitResponse

    /** 업무 상태를 완료로 변경합니다. */
    suspend fun setPostDone(projectId: String, postId: String): DoorayApiUnitResponse

    /** 업무의 상위 업무를 설정합니다. */
    suspend fun setPostParent(
        projectId: String,
        postId: String,
        parentPostId: String
    ): DoorayApiUnitResponse

    // ============ 업무 댓글 관련 API ============

    /** 업무에 댓글을 생성합니다. */
    suspend fun createPostComment(
        projectId: String,
        postId: String,
        request: CreateCommentRequest
    ): CreateCommentApiResponse

    /** 업무 댓글 목록을 조회합니다. */
    suspend fun getPostComments(
        projectId: String,
        postId: String,
        page: Int? = null,
        size: Int? = null,
        order: String? = null
    ): PostCommentListResponse

    /** 특정 업무 댓글의 상세 정보를 조회합니다. */
    suspend fun getPostComment(
        projectId: String,
        postId: String,
        logId: String
    ): PostCommentDetailResponse

    /** 업무 댓글을 수정합니다. */
    suspend fun updatePostComment(
        projectId: String,
        postId: String,
        logId: String,
        request: UpdateCommentRequest
    ): UpdateCommentResponse

    /** 업무 댓글을 삭제합니다. */
    suspend fun deletePostComment(
        projectId: String,
        postId: String,
        logId: String
    ): DeleteCommentResponse

    // ============ 프로젝트 관련 API ============
    suspend fun getProjects(
        page: Int? = null,
        size: Int? = null,
        type: String? = null,
        scope: String? = null,
        state: String? = null
    ): ProjectListResponse

    // ============ 메신저 관련 API ============

    /** 멤버를 검색합니다. */
    suspend fun searchMembers(
        name: String? = null,
        externalEmailAddresses: List<String>? = null,
        userCode: String? = null,
        idProviderUserId: String? = null,
        page: Int? = null,
        size: Int? = null
    ): MemberSearchResponse

    /** 1:1 다이렉트 메시지를 전송합니다. */
    suspend fun sendDirectMessage(request: DirectMessageRequest): DirectMessageResponse

    /** 접근 가능한 채널 목록을 조회합니다. */
    suspend fun getChannels(
        page: Int? = null,
        size: Int? = null,
        recentMonths: Int? = null
    ): ChannelListResponse

    /** 간단한 채널 목록을 조회합니다. (검색용, 대용량 데이터 방지) */
    suspend fun getSimpleChannels(
        page: Int? = null,
        size: Int? = null,
        recentMonths: Int? = null
    ): SimpleChannelListResponse

    /** 특정 채널의 상세 정보를 조회합니다. */
    suspend fun getChannel(channelId: String): Channel?

    /** 새 채널을 생성합니다. */
    suspend fun createChannel(request: CreateChannelRequest, idType: String? = null): CreateChannelResponse

    /** 채널에 멤버를 가입시킵니다. */
    suspend fun joinChannel(channelId: String, request: JoinChannelRequest): JoinChannelResponse

    /** 채널에서 멤버를 제거합니다. */
    suspend fun leaveChannel(channelId: String, request: LeaveChannelRequest): LeaveChannelResponse

    // ⚠️ 채널 로그 조회는 Dooray API에서 지원하지 않음 (보안상 제한)
    // suspend fun getChannelLogs(...): ChannelLogsResponse

    /** 채널에 메시지를 전송합니다. */
    suspend fun sendChannelMessage(
        channelId: String,
        request: SendChannelMessageRequest
    ): SendChannelMessageResponse

    /** 채널에 스레드를 생성하고 메시지를 전송합니다. */
    suspend fun createThreadAndSend(
        channelId: String,
        request: CreateThreadRequest
    ): CreateThreadResponse

    /** 채널 메시지를 수정합니다. */
    suspend fun updateChannelMessage(
        channelId: String,
        logId: String,
        request: UpdateMessageRequest
    ): UpdateMessageResponse

    /** 채널 메시지를 삭제합니다. */
    suspend fun deleteChannelMessage(
        channelId: String,
        logId: String
    ): DeleteMessageResponse

    // ============ 캘린더 관련 API ============

    /** 접근 가능한 캘린더 목록을 조회합니다. */
    suspend fun getCalendars(): CalendarListResponse

    /** 특정 캘린더의 상세 정보를 조회합니다. */
    suspend fun getCalendarDetail(calendarId: String): CalendarDetailResponse

    /** 특정 기간의 캘린더 일정을 조회합니다. */
    suspend fun getCalendarEvents(
        calendars: String? = null,
        timeMin: String,
        timeMax: String,
        postType: String? = null,
        category: String? = null
    ): CalendarEventsResponse

    /** 특정 캘린더 일정의 상세 정보를 조회합니다. */
    suspend fun getCalendarEventDetail(
        calendarId: String,
        eventId: String
    ): CalendarEventDetailResponse

    /** 캘린더에 새로운 일정을 등록합니다. */
    suspend fun createCalendarEvent(
        calendarId: String,
        request: CreateCalendarEventRequest
    ): CalendarEventCreateResponse

    // ============ Drive 관련 API ============

    /** 접근 가능한 드라이브 목록을 조회합니다. */
    suspend fun getDrives(
        projectId: String? = null,
        type: String? = null,
        scope: String? = null,
        state: String? = null
    ): DriveListResponse

    /** 특정 드라이브의 상세 정보를 조회합니다. */
    suspend fun getDriveDetail(driveId: String): DriveDetailResponse

    /** 드라이브의 파일 목록을 조회합니다. */
    suspend fun getDriveFiles(
        driveId: String,
        type: String? = null,
        subTypes: String? = null,
        parentId: String? = null,
        page: Int? = null,
        size: Int? = null
    ): DriveFileListResponse

    /** 특정 파일의 상세 정보를 조회합니다. */
    suspend fun getDriveFileDetail(driveId: String, fileId: String): DriveFileDetailResponse

    /** 파일을 업로드합니다. */
    suspend fun uploadFile(
        driveId: String,
        request: UploadFileRequest
    ): UploadFileResponse
    
    /** Base64로 파일을 업로드합니다. (MCP 도구용) */
    suspend fun uploadFileFromBase64(
        driveId: String,
        request: Base64UploadRequest
    ): UploadFileResponse

    /** 파일 내용을 다운로드합니다. (raw data) */
    suspend fun downloadFile(driveId: String, fileId: String): String

    /** 파일 메타정보를 조회합니다. */
    suspend fun getFileMetadata(driveId: String, fileId: String): DriveFileMetadataResponse

    /** 파일 또는 폴더의 이름을 변경합니다. */
    suspend fun renameFile(driveId: String, fileId: String, newName: String): RenameFileResponse

    /** 파일을 수정합니다. (새 버전 업로드) */
    suspend fun updateFile(
        driveId: String,
        fileId: String,
        request: UploadFileRequest
    ): UpdateFileResponse
    
    /** Base64로 파일을 업데이트합니다. (MCP 도구용) */
    suspend fun updateFileFromBase64(
        driveId: String,
        fileId: String,
        request: Base64UploadRequest
    ): UpdateFileResponse

    /** 파일을 삭제합니다. */
    suspend fun deleteFile(driveId: String, fileId: String): DeleteFileResponse

    /** 폴더를 생성합니다. */
    suspend fun createFolder(
        driveId: String,
        parentFolderId: String,
        request: CreateFolderRequest
    ): CreateFolderResponse

    /** 파일을 복사합니다. */
    suspend fun copyFile(
        driveId: String,
        fileId: String,
        request: CopyFileRequest
    ): CopyFileResponse

    /** 파일을 이동합니다. */
    suspend fun moveFile(
        driveId: String,
        fileId: String,
        request: MoveFileRequest
    ): MoveFileResponse
    
    /** 파일을 휴지통으로 이동합니다. */
    suspend fun moveFileToTrash(driveId: String, fileId: String): MoveFileResponse

    /** 드라이브 내 변경사항을 조회합니다. */
    suspend fun getDriveChanges(
        driveId: String,
        latestRevision: String? = null,
        fileId: String? = null,
        size: Int? = null
    ): DriveChangesResponse

    // ============ Drive Shared Link 관련 API ============

    /** 파일의 공유 링크를 생성합니다. */
    suspend fun createSharedLink(
        driveId: String,
        fileId: String,
        request: CreateSharedLinkRequest
    ): CreateSharedLinkResponse

    /** 파일에 생성된 모든 공유 링크를 조회합니다. */
    suspend fun getSharedLinks(
        driveId: String,
        fileId: String,
        valid: Boolean? = true
    ): SharedLinkListResponse

    /** 특정 공유 링크의 상세 정보를 조회합니다. */
    suspend fun getSharedLinkDetail(
        driveId: String,
        fileId: String,
        linkId: String
    ): SharedLinkDetailResponse

    /** 특정 공유 링크를 수정합니다. */
    suspend fun updateSharedLink(
        driveId: String,
        fileId: String,
        linkId: String,
        request: UpdateSharedLinkRequest
    ): SharedLinkUpdateResponse

    /** 특정 공유 링크를 삭제합니다. */
    suspend fun deleteSharedLink(
        driveId: String,
        fileId: String,
        linkId: String
    ): SharedLinkDeleteResponse
}
