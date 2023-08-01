import defaultSettings from '@/settings'
import i18n from '@/lang'
import { getLanguage } from '@/lang/index'

const title = defaultSettings.title || 'EOA Web'

export default function getPageTitle(key, fullName) {
  const realTitle = getLanguage() === 'en' ? 'EAP' : getLanguage() === 'zh' ? 'EAP' : title
  const hasKey = i18n.te(`route.${key}`)
  if (hasKey) {
    const pageName = i18n.t(`route.${key}`)
    return `${pageName} - ${realTitle}`
  }
  return fullName ? `${fullName} - ${realTitle}` : realTitle
}
