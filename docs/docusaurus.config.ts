import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
    title: 'Advanced Shop Finder Docs',
    favicon: 'img/favicon.ico',

    // Set the production url of your site here
    url: 'https://advanced-shop-finder.plugin.nikomaru.dev',
    // Set the /<baseUrl>/ pathname under which your site is served
    // For GitHub pages deployment, it is often '/<projectName>/'
    baseUrl: '/',

    // GitHub pages deployment config.
    // If you aren't using GitHub pages, you don't need these.
    organizationName: 'nlkomaru', // Usually your GitHub org/user name.
    projectName: 'advancedshopfinder', // Usually your repo name.

    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',

    // Even if you don't use internationalization, you can use this field to set
    // useful metadata like html lang. For example, if your site is Chinese, you
    // may want to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'ja',
        locales: ['ja', 'en'],
        localeConfigs: {
            en: {
                label: 'English',
            },
            ja: {
                label: '日本語',
            },
        },
    },
    plugins: [
        [
            require.resolve("@easyops-cn/docusaurus-search-local"),
            {
                indexDocs: true,
            },
        ],
    ],
    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: './sidebars.ts',
                    // Please change this to your repo.
                    // Remove this to remove the "edit this page" links.
                    routeBasePath: '/',
                    editUrl: "https://github.com/nlkomaru/advancedshopfinder/tree/master/docs"
                },
                theme: {
                    customCss: './src/css/custom.css',
                },
            } satisfies Preset.Options,
        ],
    ],

    themeConfig: {
        // Replace with your project's social card
        image: 'img/docusaurus-social-card.jpg',
        navbar: {
            title: 'My Site',
            logo: {
                alt: 'My Site Logo',
                src: 'img/logo.svg',
            },
            items: [
                {
                    type: 'localeDropdown',
                    position: 'right',
                },

                {
                    href: 'https://github.com/nlkomaru/advancedshopfinder',
                    label: 'GitHub',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: 'dark',
            links: [
                {
                    title: 'Community',
                    items: [
                        {
                            label: 'Discord',
                            href: 'https://discord.com/invite/9HdanPM',
                        },
                        {
                            label: 'X',
                            href: 'https://x.com/morinoparty',
                        },
                    ],
                },
                {
                    title: 'More',
                    items: [
                        {
                            label: 'GitHub',
                            href: 'https://github.com/nlkomaru/advancedshopfinder',
                        },
                    ],
                },
            ],
            copyright: `No right reserved. This docs under CC0. Built with Docusaurus.`,
        },
        prism: {
            theme: prismThemes.github,
            darkTheme: prismThemes.dracula,
        },
    } satisfies Preset.ThemeConfig,
};

export default config;
