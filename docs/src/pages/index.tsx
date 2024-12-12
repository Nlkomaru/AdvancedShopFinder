import React from 'react';
import { Redirect } from '@docusaurus/router';
import useBaseUrl from '@docusaurus/useBaseUrl';
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";

function Home() {
    const { siteConfig, i18n } = useDocusaurusContext()
    const { currentLocale } = i18n
    const baseDir = currentLocale === 'ja' ? `/intro/` : `/${currentLocale}/intro/`
    return <Redirect to={useBaseUrl(baseDir)} />;
}

export default Home;