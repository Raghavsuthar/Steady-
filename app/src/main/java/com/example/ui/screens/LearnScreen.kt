package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber

data class LearnArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val icon: ImageVector,
    val readingTime: String = "2 min"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    modifier: Modifier = Modifier
) {
    var activeArticle by remember { mutableStateOf<LearnArticle?>(null) }
    val bookmarkedIds = remember { mutableStateListOf<String>() }
    val readIds = remember { mutableStateListOf<String>() }

    val articles = remember {
        listOf(
            LearnArticle(
                id = "ocd_cycle",
                title = "The OCD Cycle",
                summary = "Why obsessions hook the brain and why compulsions fuel them.",
                content = "OCD is characterized by a predictable four-stage loop:\n\n1. Obsession: An intrusive thought, urge, or image enters consciousness.\n2. Distress / Anxiety: The brain misinterprets the thought as an emergency.\n3. Compulsion (Ritual): An action is taken to neutralize the fear (washing, checking, asking for reassurance, or mental rationalizing).\n4. Temporary Relief: Relief follows briefly, but the brain mistakenly learns: 'The ritual saved me from catastrophe.'\n\nConsequently, the next intrusive thought arrives with greater intensity. ERP breaks this loop by removing step 3.",
                icon = Icons.Default.Loop
            ),
            LearnArticle(
                id = "reassurance_backfires",
                title = "Why Reassurance Backfires",
                summary = "Reassurance-seeking is a compulsion disguised as comfort.",
                content = "When you feel terrified by an intrusive doubt ('Did I turn off the stove?' 'Did I offend my friend?'), asking someone 'Are you sure I'm safe?' produces immediate relief.\n\nHowever, reassurance is a compulsion. It tells your brain: 'You cannot handle uncertainty on your own.' Each time you seek reassurance, the brain's tolerance for doubt shrinks. With ERP, we practice acknowledging: 'Maybe, maybe not. I choose to sit with the doubt.'",
                icon = Icons.Default.Shield
            ),
            LearnArticle(
                id = "what_exposure_does",
                title = "What Exposure Actually Does",
                summary = "Habituation and inhibitory learning simply explained.",
                content = "Older models believed ERP worked solely through 'habituation' — waiting until anxiety dropped.\n\nModern neuroscience reveals that ERP works primarily through 'Inhibitory Learning'. You are creating a brand new safety pathway in your brain that competes with the old fear pathway. By staying in the feared situation and withholding the ritual, your brain learns: 'I expected a catastrophe, but it did not happen, and even with high anxiety, I was able to survive.'",
                icon = Icons.Default.Psychology
            ),
            LearnArticle(
                id = "myths_about_ocd",
                title = "Myths About OCD",
                summary = "Debunking common misconceptions about intrusive thoughts.",
                content = "Myth 1: 'OCD is just liking neatness and clean desks.' Reality: OCD is an exhausting, distress-driven neurobehavioral disorder.\n\nMyth 2: 'Having a bad thought means you secretly want it to happen.' Reality: Intrusive thoughts in OCD are egodystonic — they run completely opposite to your true values and character. OCD attacks what you care about most.",
                icon = Icons.Default.QuestionMark
            ),
            LearnArticle(
                id = "handling_setbacks",
                title = "Handling Setbacks",
                summary = "Spikes and bad days are a normal part of recovery.",
                content = "Recovery from OCD is never a straight diagonal line. Stress, illness, or sleep deprivation can cause sudden spikes in intrusive thoughts.\n\nA bad day does not erase the neuroplastic learning you built. When you perform a compulsion, do not berate yourself. Acknowledge it with self-compassion: 'I had a slip. That is human. The next moment is a fresh opportunity to delay or resist.'",
                icon = Icons.AutoMirrored.Filled.TrendingDown
            ),
            LearnArticle(
                id = "family_tips",
                title = "Tips for Family Members",
                summary = "Accommodation vs supportive companionship.",
                content = "Loved ones often want to help by answering reassurance questions ('Yes, your hands are clean') or participating in rituals. This is called 'family accommodation'.\n\nWhile well-intentioned, accommodation inadvertently keeps OCD alive. Better supportive language: 'I love you too much to feed OCD. I know this is uncomfortable, and I believe you can tolerate this feeling.'",
                icon = Icons.Default.People
            ),
            LearnArticle(
                id = "when_seek_help",
                title = "When to Seek Professional Help",
                summary = "Recognizing when clinical guidance is essential.",
                content = "Steady is a compassionate companion tool, but working with a licensed clinician trained in Exposure and Response Prevention (ERP) or Acceptance and Commitment Therapy (ACT) provides personalized, clinical supervision.\n\nIf OCD consumes more than an hour a day, prevents you from working, going outside, or causes profound despair, connect with a therapist or mental health service (such as Tele-MANAS 14416 in India or local crisis support).",
                icon = Icons.Default.Warning
            ),
            LearnArticle(
                id = "act_uncertainty",
                title = "Living with Uncertainty (ACT)",
                summary = "Why tolerating the unknown is the ultimate key to OCD freedom.",
                content = "Obsessive-compulsive doubt demands 100% certainty: 'Are you absolutely sure you didn't run someone over?' 'Are you 100% safe?'\n\nIn Acceptance and Commitment Therapy (ACT), we recognize that absolute certainty is an illusion. Everyone lives with micro-uncertainties every day. When we stop struggling to prove safety and instead say: 'I choose to live according to my values even while uncertainty sits in the passenger seat,' OCD loses its leverage.",
                icon = Icons.AutoMirrored.Filled.MenuBook
            ),
            LearnArticle(
                id = "mental_compulsions",
                title = "Mental Compulsions: The Invisible Rituals",
                summary = "Identifying internal checking, replay, and reassurance.",
                content = "Many people with OCD believe they don't have compulsions because they don't wash their hands or check physical locks.\n\nHowever, 'Pure O' is a misnomer — compulsions are simply mental: mentally replaying past conversations, checking how your body feels, praying repeatedly, or neutralizing bad thoughts with good words. Identifying mental rituals as compulsions is the first step toward stopping them.",
                icon = Icons.Default.Psychology
            ),
            LearnArticle(
                id = "imaginal_exposure",
                title = "Imaginal Exposure: Facing the Feared Future",
                summary = "Confronting fears that cannot be triggered in real life.",
                content = "Some fears cannot be tested directly in vivo (e.g. fears of eternal damnation, contracting a terminal illness 30 years from now, or going insane).\n\nImaginal Exposure involves writing a vivid, present-tense script of your feared scenario and reading or listening to it repeatedly without mental neutralization. Over repeated exposures, the brain learns that thoughts and words are just neural firing, not prophecies.",
                icon = Icons.Default.CheckCircle
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "ERP & OCD Insights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Clinical psychoeducation designed to empower, not reassure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            }

            items(articles, key = { it.id }) { article ->
                val isBookmarked = bookmarkedIds.contains(article.id)
                val isRead = readIds.contains(article.id)

                LearnArticleCard(
                    article = article,
                    isBookmarked = isBookmarked,
                    isRead = isRead,
                    onOpen = { activeArticle = article },
                    onToggleBookmark = {
                        if (isBookmarked) bookmarkedIds.remove(article.id)
                        else bookmarkedIds.add(article.id)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Reader Modal Bottom Sheet
        activeArticle?.let { article ->
            val isRead = readIds.contains(article.id)
            ModalBottomSheet(
                onDismissRequest = { activeArticle = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SandAccent.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = article.icon, contentDescription = null, tint = SagePrimaryDeep)
                        }
                        Text(
                            text = article.readingTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = article.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    SteadyPrimaryButton(
                        text = if (isRead) "Mark as unread" else "Mark as read",
                        onClick = {
                            if (isRead) readIds.remove(article.id)
                            else readIds.add(article.id)
                            activeArticle = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "learn_read_button"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LearnArticleCard(
    article: LearnArticle,
    isBookmarked: Boolean,
    isRead: Boolean,
    onOpen: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen,
        testTag = "article_${article.id}"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SandAccent.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = article.icon,
                    contentDescription = null,
                    tint = SagePrimaryDeep,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Read",
                            tint = SagePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    maxLines = 2
                )
            }
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) SagePrimary else MutedText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
