<?xml version="1.0" encoding="UTF-8"?>
<!--
	Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
	
	The contents of this file are subject to the Mozilla Public License
	Version 1.1 (the "License"); you may not use this file except in
	compliance with the License. You may obtain a copy of the License at
	http://www.mozilla.org/MPL/
	
	Software distributed under the License is distributed on an "AS IS"
	basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
	License for the specific language governing rights and limitations
	under the License.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ci="http://apache.org/cocoon/include/1.0"
	xmlns:source="http://apache.org/cocoon/source/1.0">

	<xsl:param name="path" />
  <xsl:param name="queryroot" />
  <xsl:param name="timestamp" />

  <xsl:template match="from[contains(text(),'@')]">
    <from sender="{normalize-space(.)}">
      <ci:include src="{$queryroot}" />
    </from>
  </xsl:template>
  
  <xsl:template match="message[not(date)]">
    <message>
      <xsl:apply-templates select="@*|node()" />
      <date><xsl:value-of select="$timestamp" /></date>
    </message>
  </xsl:template>
  
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
