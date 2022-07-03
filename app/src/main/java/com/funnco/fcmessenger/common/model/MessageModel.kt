package com.funnco.fcmessenger.common.model

import java.sql.Timestamp

class MessageModel {
    var attachmentFilepath: String? = null
    var author: UserModel? = null
    var creationTime: Timestamp? = null
    var messageContent: String? = null
    var chatId: String? = null
}