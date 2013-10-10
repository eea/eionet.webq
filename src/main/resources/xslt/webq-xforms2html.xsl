<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf bf"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:import href="xhtml.xsl"/>

    <xsl:template name="InputDateAndTime">
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="type"/>
        <xsl:param name="navindex"/>
        <xsl:param name="classes"/>

        <xsl:variable name="dataBfParams">
            <xsl:choose>
                <xsl:when test="exists(@data-bf-params) and string-length(@data-bf-params) &gt; 0">
                    <xsl:value-of select="@data-bf-params"/>,value:'<xsl:value-of select="bf:data/@bf:schema-value"/>'
                </xsl:when>
                <xsl:otherwise>date:'dd/MM/yyyy',locale:'et-ee',value:'<xsl:value-of select="bf:data/@bf:schema-value"/>'
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <input id="{$id}-value"
                name="{$name}"
                type="{$type}"
                class="{$classes}"
                tabindex="{$navindex}"
                data-bf-params="{$dataBfParams}"
                placeholder="{xf:hint/text()}"
                value="{bf:data/text()}">
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="@*" mode="copy-foreign-attributes"/>
        </input>
    </xsl:template>

</xsl:stylesheet>