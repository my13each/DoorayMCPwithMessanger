package com.my13each.dooray.mcp.tools

import com.my13each.dooray.mcp.client.DoorayClient
import com.my13each.dooray.mcp.exception.ToolException
import com.my13each.dooray.mcp.types.ToolSuccessResponse
import com.my13each.dooray.mcp.utils.JsonUtils
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

fun getProjectPostsTool(): Tool {
    return Tool(
        name = "dooray_project_list_posts",
        description = """
            두레이 프로젝트의 업무 목록을 조회합니다.

            📋 반환 정보 (경량화):
            - 기본 정보: ID, 제목, 업무번호, 상태, 우선순위, 마감일
            - 담당자: 이름과 ID만 포함 (참조자/작성자/워크플로우 제외)

            💡 상세 정보가 필요한 경우 dooray_project_get_post를 사용하세요.
        """.trimIndent(),
        inputSchema =
            Tool.Input(
                properties =
                    buildJsonObject {
                        putJsonObject("project_id") {
                            put("type", "string")
                            put("description", "프로젝트 ID (필수)")
                        }
                        putJsonObject("page") {
                            put("type", "integer")
                            put("description", "페이지 번호 (기본값: 0)")
                            put("default", 0)
                        }
                        putJsonObject("size") {
                            put("type", "integer")
                            put("description", "페이지 크기 (기본값: 20, 최대: 100)")
                            put("default", 20)
                        }
                        putJsonObject("to_member_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "담당자 멤버 ID 목록 (선택사항)")
                        }
                        putJsonObject("cc_member_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "참조자 멤버 ID 목록 (선택사항)")
                        }
                        putJsonObject("tag_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "태그 ID 목록 (선택사항)")
                        }
                        putJsonObject("parent_post_id") {
                            put("type", "string")
                            put(
                                "description",
                                "상위 업무 ID - 특정 업무의 하위 업무들만 조회 (선택사항)"
                            )
                        }
                        putJsonObject("from_member_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "작성자 멤버 ID 목록 (선택사항)")
                        }
                        putJsonObject("from_email_address") {
                            put("type", "string")
                            put("description", "작성자 이메일 주소로 필터 (선택사항)")
                        }
                        putJsonObject("to_member_size") {
                            put("type", "integer")
                            put("description", "담당자 수 필터 (0: 담당자 미지정, 1: 단독 담당) (선택사항)")
                        }
                        putJsonObject("post_number") {
                            put("type", "string")
                            put("description", "업무 번호로 필터 (선택사항)")
                        }
                        putJsonObject("post_workflow_classes") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put(
                                "description",
                                "워크플로우 클래스 (backlog, registered, working, closed) (선택사항)"
                            )
                        }
                        putJsonObject("post_workflow_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "워크플로우 ID 목록으로 필터 (선택사항)")
                        }
                        putJsonObject("milestone_ids") {
                            put("type", "array")
                            putJsonObject("items") { put("type", "string") }
                            put("description", "마일스톤 ID 목록 (선택사항)")
                        }
                        putJsonObject("subjects") {
                            put("type", "string")
                            put("description", "업무 제목 검색어 (선택사항)")
                        }
                        putJsonObject("created_at") {
                            put("type", "string")
                            put("description", "생성시간 필터 (DATE_PATTERN, 예: 2024-01-01T00:00:00+09:00~2024-12-31T23:59:59+09:00) (선택사항)")
                        }
                        putJsonObject("updated_at") {
                            put("type", "string")
                            put("description", "업데이트 시간 필터 (DATE_PATTERN) (선택사항)")
                        }
                        putJsonObject("due_at") {
                            put("type", "string")
                            put("description", "마감시간 필터 (DATE_PATTERN) (선택사항)")
                        }
                        putJsonObject("order") {
                            put("type", "string")
                            put(
                                "description",
                                "정렬 조건 (postDueAt, postUpdatedAt, createdAt, 역순은 앞에 '-' 추가) (선택사항)"
                            )
                        }
                    },
                required = listOf("project_id")
            ),
        outputSchema = null,
        annotations = null
    )
}

fun getProjectPostsHandler(
    doorayClient: DoorayClient
): suspend (CallToolRequest) -> CallToolResult {
    return { request ->
        try {
            val projectId = request.arguments["project_id"]?.jsonPrimitive?.content

            if (projectId == null) {
                val errorResponse =
                    ToolException(
                        type = ToolException.PARAMETER_MISSING,
                        message = "project_id 파라미터가 필요합니다. 프로젝트 ID를 입력하세요.",
                        code = "MISSING_PROJECT_ID"
                    )
                        .toErrorResponse()

                CallToolResult(content = listOf(TextContent(JsonUtils.toJsonString(errorResponse))))
            } else {
                val page = request.arguments["page"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                val size = request.arguments["size"]?.jsonPrimitive?.content?.toIntOrNull() ?: 20

                // 배열 파라미터 처리
                val fromMemberIds =
                    request.arguments["from_member_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val toMemberIds =
                    request.arguments["to_member_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val ccMemberIds =
                    request.arguments["cc_member_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val tagIds =
                    request.arguments["tag_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val postWorkflowClasses =
                    request.arguments["post_workflow_classes"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val postWorkflowIds =
                    request.arguments["post_workflow_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }
                val milestoneIds =
                    request.arguments["milestone_ids"]?.let { element ->
                        JsonUtils.parseStringArray(element.toString())
                    }

                // 단일 값 파라미터 처리
                val fromEmailAddress = request.arguments["from_email_address"]?.jsonPrimitive?.content
                val toMemberSize = request.arguments["to_member_size"]?.jsonPrimitive?.content?.toIntOrNull()
                val parentPostId = request.arguments["parent_post_id"]?.jsonPrimitive?.content
                val postNumber = request.arguments["post_number"]?.jsonPrimitive?.content
                val subjects = request.arguments["subjects"]?.jsonPrimitive?.content
                val createdAt = request.arguments["created_at"]?.jsonPrimitive?.content
                val updatedAt = request.arguments["updated_at"]?.jsonPrimitive?.content
                val dueAt = request.arguments["due_at"]?.jsonPrimitive?.content
                val order = request.arguments["order"]?.jsonPrimitive?.content

                val response =
                    doorayClient.getPosts(
                        projectId = projectId,
                        page = page,
                        size = size,
                        fromMemberIds = fromMemberIds,
                        fromEmailAddress = fromEmailAddress,
                        toMemberIds = toMemberIds,
                        toMemberSize = toMemberSize,
                        ccMemberIds = ccMemberIds,
                        tagIds = tagIds,
                        parentPostId = parentPostId,
                        postNumber = postNumber,
                        postWorkflowClasses = postWorkflowClasses,
                        postWorkflowIds = postWorkflowIds,
                        milestoneIds = milestoneIds,
                        subjects = subjects,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        dueAt = dueAt,
                        order = order
                    )

                if (response.header.isSuccessful) {
                    // Post를 PostSummary로 변환 (경량화)
                    val summaries = response.result.map { post ->
                        com.my13each.dooray.mcp.types.PostSummary(
                            id = post.id,
                            subject = post.subject,
                            taskNumber = post.taskNumber,
                            workflowClass = post.workflowClass,
                            workflow = post.workflow,
                            assignees = post.users.to.mapNotNull { it.member }, // Member (이름/ID)만 추출
                            priority = post.priority,
                            dueDate = post.dueDate,
                            createdAt = post.createdAt,
                            updatedAt = post.updatedAt
                        )
                    }

                    val pageInfo = if (page == 0) "첫 번째 페이지" else "${page + 1}번째 페이지"

                    val nextStepHint =
                        if (summaries.isNotEmpty()) {
                            "\n\n💡 다음 단계: 특정 업무의 상세 정보를 보려면 dooray_project_get_post를 사용하세요."
                        } else {
                            if (page == 0) "\n\n📋 조회 결과가 없습니다. 필터 조건을 확인해주세요."
                            else "\n\n📄 더 이상 업무가 없습니다."
                        }

                    val successResponse =
                        ToolSuccessResponse(
                            data = summaries,
                            message =
                                "📋 프로젝트 업무 목록을 성공적으로 조회했습니다 ($pageInfo, 총 ${summaries.size}개)$nextStepHint"
                        )

                    CallToolResult(
                        content = listOf(TextContent(JsonUtils.toJsonString(successResponse)))
                    )
                } else {
                    val errorResponse =
                        ToolException(
                            type = ToolException.API_ERROR,
                            message = response.header.resultMessage,
                            code = "DOORAY_API_${response.header.resultCode}"
                        )
                            .toErrorResponse()

                    CallToolResult(
                        content = listOf(TextContent(JsonUtils.toJsonString(errorResponse)))
                    )
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ToolException(
                    type = ToolException.INTERNAL_ERROR,
                    message = "내부 오류가 발생했습니다: ${e.message}",
                    details = e.stackTraceToString()
                )
                    .toErrorResponse()

            CallToolResult(content = listOf(TextContent(JsonUtils.toJsonString(errorResponse))))
        }
    }
}
