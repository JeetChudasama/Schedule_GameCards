package com.example.listandgamecards.models

import com.google.gson.annotations.SerializedName

data class GameRoot(
    val entry: Entry,
)

data class Entry(
    val title: String,
    @SerializedName("game_card_config")
    val gameCardConfig: GameCardConfig,
    @SerializedName("past_game_card")
    val pastGameCard: PastGameCard,
    @SerializedName("upcoming_game")
    val upcomingGame: UpcomingGame,
    @SerializedName("future_game")
    val futureGame: FutureGame,
    val tags: List<Any?>,
    val locale: String,
    val uid: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("ACL")
    val acl: Map<String, Any>,
    @SerializedName("_version")
    val version: Long,
    @SerializedName("_in_progress")
    val inProgress: Boolean,
    @SerializedName("promotion_cards")
    val promotionCards: List<PromotionCard>,
    @SerializedName("publish_details")
    val publishDetails: List<PublishDetail5>,
)

data class GameCardConfig(
    @SerializedName("focus_card")
    val focusCard: Long,
    @SerializedName("future_game_count")
    val futureGameCount: Long,
    @SerializedName("past_game_count")
    val pastGameCount: Long,
)

data class PastGameCard(
    @SerializedName("background_image")
    val backgroundImage: BackgroundImage,
    val button: Button,
)

data class BackgroundImage(
    val uid: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("file_size")
    val fileSize: String,
    val tags: List<Any?>,
    val filename: String,
    val url: String,
    @SerializedName("ACL")
    val acl: List<Any?>,
    @SerializedName("is_dir")
    val isDir: Boolean,
    @SerializedName("parent_uid")
    val parentUid: String,
    @SerializedName("_version")
    val version: Long,
    val title: String,
    val dimension: Dimension,
    @SerializedName("publish_details")
    val publishDetails: List<PublishDetail>,
)

data class Dimension(
    val height: Long,
    val width: Long,
)

data class PublishDetail(
    val environment: String,
    val locale: String,
    val time: String,
    val user: String,
    val version: Long,
)

data class Button(
    @SerializedName("cta_text")
    val ctaText: String,
    val icons: Icons,
    @SerializedName("cta_link")
    val ctaLink: String,
)

data class Icons(
    @SerializedName("leading_icon")
    val leadingIcon: Any?,
    @SerializedName("trailing_icon")
    val trailingIcon: Any?,
)

data class UpcomingGame(
    @SerializedName("background_image")
    val backgroundImage: BackgroundImage2,
    val button: Button2,
)

data class BackgroundImage2(
    val uid: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("file_size")
    val fileSize: String,
    val tags: List<Any?>,
    val filename: String,
    val url: String,
    @SerializedName("ACL")
    val acl: List<Any?>,
    @SerializedName("is_dir")
    val isDir: Boolean,
    @SerializedName("parent_uid")
    val parentUid: String,
    @SerializedName("_version")
    val version: Long,
    val title: String,
    val dimension: Dimension2,
    @SerializedName("publish_details")
    val publishDetails: List<PublishDetail2>,
)

data class Dimension2(
    val height: Long,
    val width: Long,
)

data class PublishDetail2(
    val environment: String,
    val locale: String,
    val time: String,
    val user: String,
    val version: Long,
)

data class Button2(
    @SerializedName("cta_text")
    val ctaText: String,
    val icons: Icons2,
    @SerializedName("cta_link")
    val ctaLink: String,
)

data class Icons2(
    @SerializedName("trailing_icon")
    val trailingIcon: TrailingIcon,
    @SerializedName("leading_icon")
    val leadingIcon: Any?,
)

data class TrailingIcon(
    val uid: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("file_size")
    val fileSize: String,
    val tags: List<Any?>,
    val filename: String,
    val url: String,
    @SerializedName("ACL")
    val acl: List<Any?>,
    @SerializedName("is_dir")
    val isDir: Boolean,
    @SerializedName("parent_uid")
    val parentUid: String,
    @SerializedName("_version")
    val version: Long,
    val title: String,
    val dimension: Dimension3,
    @SerializedName("publish_details")
    val publishDetails: List<PublishDetail3>,
)

data class Dimension3(
    val height: Long,
    val width: Long,
)

data class PublishDetail3(
    val environment: String,
    val locale: String,
    val time: String,
    val user: String,
    val version: Long,
)

data class FutureGame(
    @SerializedName("background_image")
    val backgroundImage: BackgroundImage3,
    val button: Button3,
)

data class BackgroundImage3(
    val uid: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("file_size")
    val fileSize: String,
    val tags: List<Any?>,
    val filename: String,
    val url: String,
    @SerializedName("ACL")
    val acl: List<Any?>,
    @SerializedName("is_dir")
    val isDir: Boolean,
    @SerializedName("parent_uid")
    val parentUid: String,
    @SerializedName("_version")
    val version: Long,
    val title: String,
    val dimension: Dimension4,
    @SerializedName("publish_details")
    val publishDetails: List<PublishDetail4>,
)

data class Dimension4(
    val height: Long,
    val width: Long,
)

data class PublishDetail4(
    val environment: String,
    val locale: String,
    val time: String,
    val user: String,
    val version: Long,
)

data class Button3(
    @SerializedName("cta_text")
    val ctaText: String,
    val icons: Icons3,
    @SerializedName("cta_link")
    val ctaLink: String,
)

data class Icons3(
    @SerializedName("leading_icon")
    val leadingIcon: Any?,
    @SerializedName("trailing_icon")
    val trailingIcon: Any?,
)

data class PromotionCard(
    val position: Long,
    @SerializedName("_metadata")
    val metadata: Metadata,
    val card: List<Card>,
)

data class Metadata(
    val uid: String,
)

data class Card(
    val uid: String,
    @SerializedName("_content_type_uid")
    val contentTypeUid: String,
)

data class PublishDetail5(
    val environment: String,
    val locale: String,
    val time: String,
    val user: String,
    val version: Long,
)


