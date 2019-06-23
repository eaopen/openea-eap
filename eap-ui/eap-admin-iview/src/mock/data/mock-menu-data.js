export const mockmenu = [
  {
    'path': '/multilevel',
    'name': 'multilevel',
    'meta': {
      'icon': 'md-menu',
      'title': '多级菜单'
    },
    'component': 'Main',
    'children': [
      {
        'path': '/level_2_1',
        'name': 'level_2_1',
        'meta': {
          'icon': 'md-funnel',
          'title': '二级-1'
        },
        'component': 'multilevel/level-2-1'
      }
    ]
  }
]
