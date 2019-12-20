/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.addons

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import mozilla.components.feature.addons.Addon
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.showToolbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A fragment to show the details of an add-on.
 */
class AddonDetailsFragment : Fragment() {

    private val addon: Addon by lazy {
        AddonDetailsFragmentArgs.fromBundle(requireNotNull(arguments)).addon
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.frament_add_on_details, container, false)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        bind(addon, rootView)
    }

    private fun bind(addon: Addon, rootView: View) {

        val title = addon.translatableName.translate()

        showToolbar(title)

        bindDetails(addon, rootView)

        bindAuthors(addon, rootView)

        bindVersion(addon, rootView)

        bindLastUpdated(addon, rootView)

        bindWebsite(addon, rootView)

        bindRating(addon, rootView)
    }

    private fun bindRating(addon: Addon, rootView: View) {
        addon.rating?.let {
            val ratingView = rootView.findViewById<RatingBar>(R.id.rating_view)
            val userCountView = rootView.findViewById<TextView>(R.id.users_count)

            val ratingContentDescription =
                getString(R.string.mozac_feature_addons_rating_content_description)
            ratingView.contentDescription = String.format(ratingContentDescription, it.average)
            ratingView.rating = it.average

            userCountView.text = getFormattedAmount(it.reviews)
        }
    }

    private fun bindWebsite(addon: Addon, rootView: View) {
        rootView.findViewById<View>(R.id.home_page_text).setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW).setData(Uri.parse(addon.siteUrl))
            startActivity(intent)
        }
    }

    private fun bindLastUpdated(addon: Addon, rootView: View) {
        val lastUpdatedView = rootView.findViewById<TextView>(R.id.last_updated_text)
        lastUpdatedView.text = formatDate(addon.updatedAt)
    }

    private fun bindVersion(addon: Addon, rootView: View) {
        val versionView = rootView.findViewById<TextView>(R.id.version_text)
        versionView.text = addon.version
    }

    private fun bindAuthors(addon: Addon, rootView: View) {
        val authorsView = rootView.findViewById<TextView>(R.id.author_text)

        val authorText = addon.authors.joinToString { author ->
            author.name + " \n"
        }

        authorsView.text = authorText
    }

    private fun bindDetails(addon: Addon, rootView: View) {
        val detailsView = rootView.findViewById<TextView>(R.id.details)
        val detailsText = addon.translatableDescription.translate()

        val parsedText = detailsText.replace("\n", "<br/>")
        val text = HtmlCompat.fromHtml(parsedText, HtmlCompat.FROM_HTML_MODE_COMPACT)

        detailsView.text = text
        detailsView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun formatDate(text: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return DateFormat.getDateInstance().format(formatter.parse(text)!!)
    }
}
