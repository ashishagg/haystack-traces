/*
 *  Copyright 2017 Expedia, Inc.
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */

package com.expedia.www.haystack.trace.reader.stores.readers.es.query

import com.expedia.open.tracing.api.FieldValuesRequest
import com.expedia.www.haystack.trace.commons.config.entities.WhitelistIndexFieldConfiguration
import io.searchbox.core.Search
import org.elasticsearch.search.builder.SearchSourceBuilder

class FieldValuesQueryGenerator(indexNamePrefix: String,
                                indexType: String,
                                nestedDocName: String,
                                indexConfiguration: WhitelistIndexFieldConfiguration) extends QueryGenerator(nestedDocName, indexConfiguration) {
  def generate(request: FieldValuesRequest): Search = {
    new Search.Builder(buildQueryString(request))
      .addIndex(s"$indexNamePrefix*")
      .addType(indexType)
      .build()
  }

  private def buildQueryString(request: FieldValuesRequest): String = {
    val query = createQuery(request.getFiltersList)
    if (query.filter().size() > 0) {
      new SearchSourceBuilder()
        .query(query)
        .aggregation(createNestedAggregationQuery(request.getFieldName.toLowerCase))
        .size(0)
        .toString
    } else {
      new SearchSourceBuilder()
        .aggregation(createNestedAggregationQuery(request.getFieldName.toLowerCase()))
        .size(0)
        .toString
    }
  }
}
